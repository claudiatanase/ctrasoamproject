#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/module.h>
#include <linux/list.h>
#include <linux/slab.h>
#include "interceptor.h"

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




asmlinkage long my_sys_mkdir(const char __user *pathname, int mode) {

	//send interesting data to server

	//call old sys_mkdir
	return sys_mkdir(pathname, mode);
}

asmlinkage long my_sys_open(const char __user *filename, int flags, int mode) {

		//send interesting data to server

		//call old syscall
		return sys_open(filename, int flags, mode);
}

asmlinkage long my_sys_creat(const char __user * pathname, int mode) {

	//send interesting data to server

	//call old syscall
	return sys_creat(pathname, mode);
}

asmlinkage ssize_t my_sys_write(unsigned int fd, const char __user * buf, size_t count) {

	//send interesting data to server

	//call old syscall
	return sys_write(fd, buf, count);
}

asmlinkage long my_sys_link(const char __user *oldname, const char __user *newname) {

	//send interesting data to server

	//call old syscall
	return sys_link(oldname, newname);
}

asmlinkage long my_sys_unlink(const char __user *pathname) {

	//send interesting data to server

	//call old syscall
	return sys_unlink(pathname);
}


asmlinkage long my_sys_mknod(const char __user *filename, int mode, unsigned dev) {

	//send interesting data to server

	//call old syscall
	return sys_mknod(filename, mode, dev);
}

asmlinkage long my_sys_rmdir(const char __user *pathname) {
	
	//send interesting data to server

	//call old syscall
	return sys_rmdir(pathname);
}

asmlinkage long my_sys_rename(const char __user *oldname, const char __user *newname) {

	//send interesting data to server

	//call old syscall
	return sys_rename(oldname, newname);
}






//functia care face interceptarea efectiva 
static asmlinkage long interceptor(struct pt_regs regs){
    long ret;
	        
	printk(KERN_DEBUG "Am interceptat apelul de sistem %ld\n", regs.eax);
	
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
		
		//trebuie sa opresc interceptarea functiei => restaurez functia initiala
		sys_call_table[syscall] = old_sys_calls[syscall];
	
		return 0;
    }
    

	return 0;
}


//initalizarea modulului meu
static int my_module_init(void) {
    	int i;

	printk(KERN_DEBUG "My module init\n"); 

	//inlocuiesc in tabela de apeluri de sistem apelul 0 cu my_syscall
	old_sys_calls[MY_SYSCALL_NO] = sys_call_table[MY_SYSCALL_NO];
    sys_call_table[MY_SYSCALL_NO] = my_syscall;
	    
	//si copiez pt a putea reface mai tarziu , tabela de apeluri de sistem
	for (i = 1; i < NR_syscalls; i++) {
		old_sys_calls[i] = sys_call_table[i];
	}      

	//inlocuiesc apelul de sistem initial cu apelul meu de interceptare	
	sys_call_table[__NR_open] = my_sys_open;	
	sys_call_table[__NR_write] = my_sys_write;	
	sys_call_table[__NR_creat] = my_sys_creat;
	sys_call_table[__NR_link] = my_sys_link;
	sys_call_table[__NR_unlink] = my_sys_unlink;
	sys_call_table[__NR_mknod] = my_sys_mknod;
	sys_call_table[__NR_chown] = interceptor;
	sys_call_table[__NR_rename] = my_sys_rename;
	sys_call_table[__NR_mkdir] = my_sys_mkdir;
	sys_call_table[__NR_rmdir] = my_sys_rmdir;	
    
	return 0;
}

static void my_module_exit(void) {
	int i;

	//la iesire am grija sa refac tabela veche de apeluri de sistem
   	sys_call_table[MY_SYSCALL_NO] = old_sys_calls[MY_SYSCALL_NO];
	
 
   	for (i = 1; i < NR_syscalls; i++){
		sys_call_table[i] = old_sys_calls[i];
	}
    
	
	printk(KERN_DEBUG "My module exit\n");    
}

module_init(my_module_init);
module_exit(my_module_exit);
    



