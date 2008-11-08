#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/module.h>
#include <linux/list.h>
#include <linux/slab.h>
#include "sci_lin.h"

MODULE_DESCRIPTION("Interceptare apeluri sistem");
MODULE_AUTHOR("Claudia Tanase");
MODULE_LICENSE("GPL");

//importam tabela apelurilor de sistem
extern void *sys_call_table[];

//prototipul pentru apelul meu de system
static asmlinkage long (* old_call) (struct pt_regs);

//date despre apelurile de sistem interceptate
//-- sunt tinute intr-un vector deoarece
//numarul apelurilor de sistem este mic
typedef asmlinkage long (*old_sys_call)(struct pt_regs);
old_sys_call old_sys_calls[NR_syscalls];









static int add_moni(pid_t pid, int syscall)
{
        struct monitor_list *ple = (struct monitor_list*) kmalloc(sizeof(struct monitor_list), GFP_KERNEL);

        if (!ple)
                return -ENOMEM;

        INIT_LIST_HEAD(&ple->list);
        ple->pid = pid;
		ple->syscall = syscall;
        list_add(&ple->list, &my_moni_list);

        return 0;
}

static int del_moni(pid_t pid, int syscall)
{
        struct list_head *i, *n;
        struct monitor_list *ple;

        list_for_each_safe(i, n, &my_moni_list) {
                ple = list_entry(i, struct monitor_list, list);
                if (((ple->pid == pid) || (pid == 0)) && ((ple->syscall == syscall) || (syscall == 0) )) {
						list_del(i);
                        kfree(ple);
                        return 0;
                }
        }

        return -EINVAL;
}

static int find_moni(pid_t pid, int syscall)
{	
		struct list_head *i;
        struct monitor_list *ple;

        list_for_each(i, &my_moni_list) {
                ple = list_entry(i, struct monitor_list, list);
                if ((ple->pid == pid) && ((ple->syscall == syscall) ||(syscall == 0) )) {
						return 1;
                }
        }      

        return 0;
}


static int destroy_list(void)
{
        struct list_head *i, *n;
        struct monitor_list *ple;

        list_for_each_safe(i, n, &my_moni_list) {
                ple = list_entry(i, struct monitor_list, list);
                list_del(i);
                kfree(ple);
        }

		return 0;
}






//functia care face interceptarea efectiva 
static asmlinkage long interceptor(struct pt_regs regs){
    	long ret;
	struct list_head *i;
	struct monitor_list *ple;
        
	//printk(KERN_DEBUG "Am interceptat apelul de sistem");
	
	//apelez vechiul apel de system
    	old_call = old_sys_calls[regs.eax];
    	ret = old_call(regs);

	return ret;
}

//rutina care imi inlocuieste vechiul apel de sistem cu cel nou 
asmlinkage long my_syscall(int cmd, int syscall, int pid){

	//daca introduc un syscall in afara range-ului => eroare
	if(syscall <=0 || syscall >= NR_syscalls) {
		return -EINVAL;
	}


	//INTERCEPTARE
	if(cmd == REQUEST_SYSCALL_INTERCEPT){
		//Utilizatorul neprivilegiat nu poate intercepta un apel de sistem
		if(current->uid != 0)
			return -EPERM;
		
		//daca am interceptat deja apelul de sistem => eroare
		if(sys_call_table[syscall] == interceptor)
			return -EBUSY;

		//nu pot sa interceptez apelul de sistem exit pt ca il interceptez la incarcarea modulului
		if(sys_call_table[syscall] == my_exit)
			return -EINVAL;
	    
		//inlocuiesc apelul de sistem initial cu apelul meu de interceptare
		old_sys_calls[syscall] = sys_call_table[syscall];
		sys_call_table[syscall] = interceptor;
		return 0;
    	}

   	//DEINTERCEPTARE
    	if(cmd == REQUEST_SYSCALL_RELEASE){
		//Utilizatorul neprivilegiat nu poate deintercepta un apel de sistem
		if(current->uid != 0)
			return -EPERM;
			
		//nu pot deintercepta un apel pe care nu lam interceptat
	    	if(sys_call_table[syscall] != interceptor)
			return -EINVAL; 	

		//nu pot deintercepta apelul de sistem exit => eroare
		if(sys_call_table[syscall] == my_exit)
			return -EINVAL;
	    
		//opresc monitorizarile pt apelul de sistem dat
		del_moni(0,syscall);

	    //trebuie sa opresc interceptarea functiei => restaurez functia initiala
		sys_call_table[syscall] = old_sys_calls[syscall];
		return 0;
    }
    

	return 0;
}



//initalizarea modulului meu
static int my_module_init(void){
    	int i;

	printk(KERN_DEBUG "My module init\n"); 

	//inlocuiesc in tabela de apeluri de sistem apelul 0 cu my_syscall
	old_sys_calls[MY_SYSCALL_NO] = sys_call_table[MY_SYSCALL_NO];
    	sys_call_table[MY_SYSCALL_NO] = my_syscall;

	    
	//si copiez pt a putea reface mai tarziu , tabela de apeluri de sistem
	for (i = 1; i < NR_syscalls; i++) {
		old_sys_calls[i] = sys_call_table[i];
	}      
    
	return 0;
}

static void my_module_exit(void){
    	int i;

	//la iesire am grija sa refac tabela veche de apeluri de sistem
    	sys_call_table[MY_SYSCALL_NO] = old_sys_calls[MY_SYSCALL_NO];
	sys_call_table[__NR_exit] = old_exit;
 
    	for (i = 1; i < NR_syscalls; i++){
	    sys_call_table[i] = old_sys_calls[i];
	}
    
	
	printk(KERN_DEBUG "My module exit\n");    
}

module_init(my_module_init);
module_exit(my_module_exit);
    



