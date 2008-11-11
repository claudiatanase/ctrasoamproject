#include <linux/kernel.h>
#include <linux/init.h>
#include <linux/module.h>
#include <linux/list.h>
#include <linux/slab.h>
#include <linux/syscalls.h>
#include <linux/netlink.h>
#include <asm/mach-default/irq_vectors_limits.h>
#include <linux/skbuff.h>
#include <linux/socket.h>
#include <net/sock.h>
#include "interceptor.h"

MODULE_DESCRIPTION("Interceptare apeluri sistem");
MODULE_AUTHOR("Claudia Tanase");
MODULE_LICENSE("GPL");

//importam tabela apelurilor de sistem
extern void *sys_call_table[];

static asmlinkage long (*old_sys_mkdir)(const char __user *pathname, int mode);
static asmlinkage long (*old_sys_open)(const char __user *filename, int flags, int mode);
static asmlinkage long (*old_sys_creat)(const char __user * pathname, int mode);
static asmlinkage ssize_t (*old_sys_write)(unsigned int fd, const char __user * buf, size_t count);
static asmlinkage long (*old_sys_link)(const char __user *oldname, const char __user *newname);
static asmlinkage long (*old_sys_unlink)(const char __user *pathname);
static asmlinkage long (*old_sys_mknod)(const char __user *filename, int mode, unsigned dev);
static asmlinkage long (*old_sys_rmdir)(const char __user *pathname);
static asmlinkage long (*old_sys_rename)(const char __user *oldname, const char __user *newname);


//prototipul pentru apelul meu de system
static asmlinkage long (* old_call) (struct pt_regs);

//date despre apelurile de sistem interceptate
//-- sunt tinute intr-un vector deoarece
//numarul apelurilor de sistem este mic
typedef asmlinkage long (*old_sys_call)(struct pt_regs);
old_sys_call old_sys_calls[NR_syscalls];

long nr_syscalls = 0;



struct sock *nl_sk = NULL;
u32 userspace_pid;
struct sk_buff *skb = NULL;
struct nlmsghdr *nlh = NULL;

u8 *payload = NULL;
static void netlink_test(void);
static void nl_data_ready (struct sock *sk, int len);
static int debug = 0;

struct sockaddr_nl *nladdr=NULL;
struct iovec *iov=NULL;
struct msghdr *msg=NULL;
int err,pos;
int pid,size;



asmlinkage long my_sys_mkdir(const char __user *pathname, int mode) {

	//send interesting data to server
	nr_syscalls++;

	//call old sys_mkdir
	return old_sys_mkdir(pathname, mode);
	//return 0;
}

asmlinkage long my_sys_open(const char __user *filename, int flags, int mode) {

		//send interesting data to server
		nr_syscalls++;

		//call old syscall
		return old_sys_open(filename, flags, mode);
		//return 0;

}

asmlinkage long my_sys_creat(const char __user * pathname, int mode) {

	//send interesting data to server
	nr_syscalls++;

	//call old syscall
	return old_sys_creat(pathname, mode);
	//return 0;
}

asmlinkage ssize_t my_sys_write(unsigned int fd, const char __user * buf, size_t count) {

	//send interesting data to server
	nr_syscalls++;

	//call old syscall
	//return old_sys_write(fd, buf, count);
	return 0;
}

asmlinkage long my_sys_link(const char __user *oldname, const char __user *newname) {

	//send interesting data to server

	//call old syscall
	return old_sys_link(oldname, newname);
	//return 0;
}

asmlinkage long my_sys_unlink(const char __user *pathname) {

	//send interesting data to server
	nr_syscalls++;

	//call old syscall
	return old_sys_unlink(pathname);
	//return 0;
}


asmlinkage long my_sys_mknod(const char __user *filename, int mode, unsigned dev) {

	//send interesting data to server
	nr_syscalls++;

	//call old syscall
	return old_sys_mknod(filename, mode, dev);	
	//return 0;
}

asmlinkage long my_sys_rmdir(const char __user *pathname) {
	
	//send interesting data to server
	nr_syscalls++;

	//call old syscall
	return old_sys_rmdir(pathname);
	//return 0;
}

asmlinkage long my_sys_rename(const char __user *oldname, const char __user *newname) {

	//send interesting data to server
	nr_syscalls++;

	//call old syscall
	return old_sys_rename(oldname, newname);
	//return 0;
}






//functia care face interceptarea efectiva 
static asmlinkage long interceptor(struct pt_regs regs){
    long ret;
	        
	//printk(KERN_DEBUG "Am interceptat apelul de sistem %ld\n", regs.eax);
	nr_syscalls++;

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




void input (struct sock *sk, int len)
{
 
 while ((skb = skb_dequeue(&sk->receive_queue)) != NULL) {
	/ * process netlink message pointed by skb->data * /
	nlh = (struct nlmsghdr *)skb->data;
	payload = NLMSG_DATA(nlh);
	printk("%s: received netlink message payload:%s\n", __FUNCTION__, NLMSG_DATA(nlh));
	/ * process netlink message with header pointed by
	* nlh	and payload pointed by payload
	* /
 }
}


void output(u32 pid) {

	NETLINK_CB(skb).groups = 0; /* not in mcast group */
	NETLINK_CB(skb).pid = 0;      /* from kernel */
	NETLINK_CB(skb).dst_pid = pid;
	NETLINK_CB(skb).dst_groups = 0;  /* unicast */

	netlink_unicast(nl_sk, skb, pid, MSG_DONTWAIT);
}

void input2 (struct sock *sk, int len) {

	nlh = (struct nlmsghdr *)skb->data;
	printk("%s: received netlink message payload:%s\n", __FUNCTION__, NLMSG_DATA(nlh));
	userspace_pid = nlh->nlmsg_pid; /*pid of sending process */

	output(userspace_pid);
}


void nl_data_ready (struct sock *sk, int len)
{
	wake_up_interruptible(sk->sleep);
}

void netlink_test() {
 
	nl_sk = netlink_kernel_create(NETLINK_TEST, nl_data_ready);
	if (nl_sk<0)
		printk(KERN_INFO "ERROR IN NETLINK CREATION");

	skb = skb_recv_datagram(nl_sk, 0, 0, &err);

	nlh = (struct nlmsghdr *)skb->data;

	printk(KERN_INFO ": received netlink message payload: %s\n", NLMSG_DATA(nlh));

	pid=nlh->nlmsg_pid;

	/*just to see the nlh header information*/
	printk(KERN_INFO ": nl Message Length: %d\n",nlh->nlmsg_len);
	printk(KERN_INFO ": nl Message type: %d\n",nlh->nlmsg_type);
	printk(KERN_INFO ": nl Message flag: %d\n",nlh->nlmsg_flags);
	printk(KERN_INFO ": nl Message seq: %d\n",nlh->nlmsg_seq);
	printk(KERN_INFO ": nl Message pid: %d\n",nlh->nlmsg_pid);

	/* kernel is changing the nlh's payload */
	printk (KERN_INFO "Filling data\n");
	strcpy(NLMSG_DATA(nlh),"This is kernel's Message!\n");
	printk (KERN_INFO "Filled data\n");

	NETLINK_CB(skb).groups = 0;
	NETLINK_CB(skb).pid = 0;
	NETLINK_CB(skb).dst_pid = pid;
	NETLINK_CB(skb).dst_groups = 0;
	printk(KERN_INFO "FILLED DATA\n");
	
	netlink_unicast(nl_sk, skb, pid, MSG_DONTWAIT);
	printk(KERN_INFO "DATA sent\n");
	
	kfree(skb);
	sock_release(nl_sk->socket);
}




//initalizarea modulului meu
static int my_module_init(void) {
    //int i;

	nr_syscalls = 0;
	printk(KERN_DEBUG "My module init: nr_syscalls=%ld\n", nr_syscalls); 
	
	netlink_test();

#if 0
	//inlocuiesc in tabela de apeluri de sistem apelul 0 cu my_syscall
	old_sys_calls[MY_SYSCALL_NO] = sys_call_table[MY_SYSCALL_NO];
    sys_call_table[MY_SYSCALL_NO] = my_syscall;
	    
	//si copiez pt a putea reface mai tarziu , tabela de apeluri de sistem
	for (i = 1; i < NR_syscalls; i++) {
		old_sys_calls[i] = sys_call_table[i];
	}      

	//inlocuiesc apelul de sistem initial cu apelul meu de interceptare	
	/*
	old_sys_open = sys_call_table[__NR_open];
	sys_call_table[__NR_open] = my_sys_open;
	old_sys_write = sys_call_table[__NR_write];
	sys_call_table[__NR_write] = my_sys_write;
	old_sys_creat = sys_call_table[__NR_creat];
	sys_call_table[__NR_creat] = my_sys_creat;
	old_sys_link = sys_call_table[__NR_link];
	sys_call_table[__NR_link] = my_sys_link;
	old_sys_unlink = sys_call_table[__NR_unlink];
	sys_call_table[__NR_unlink] = my_sys_unlink;
	old_sys_mknod = sys_call_table[__NR_mknod];
	sys_call_table[__NR_mknod] = my_sys_mknod;
	//old_sys_chown = sys_call_table[__NR_chown];
	sys_call_table[__NR_chown] = interceptor;
	old_sys_rename = sys_call_table[__NR_rename];
	sys_call_table[__NR_rename] = my_sys_rename;
	old_sys_mkdir = sys_call_table[__NR_mkdir];
	sys_call_table[__NR_mkdir] = my_sys_mkdir;
	old_sys_rmdir = sys_call_table[__NR_rmdir];
	sys_call_table[__NR_rmdir] = my_sys_rmdir;
	*/

	
	sys_call_table[__NR_open] = interceptor;	
	sys_call_table[__NR_write] = interceptor;	
	sys_call_table[__NR_creat] = interceptor;
	sys_call_table[__NR_link] = interceptor;
	sys_call_table[__NR_unlink] = interceptor;
	sys_call_table[__NR_mknod] = interceptor;
	sys_call_table[__NR_chown] = interceptor;
	sys_call_table[__NR_rename] = interceptor;
	sys_call_table[__NR_mkdir] = interceptor;
	sys_call_table[__NR_rmdir] = interceptor;
#endif /* 0 */
    
	return 0;
}

static void my_module_exit(void) {
	int i;

#if 0
	//la iesire am grija sa refac tabela veche de apeluri de sistem
   	sys_call_table[MY_SYSCALL_NO] = old_sys_calls[MY_SYSCALL_NO];
	
 
   	for (i = 1; i < NR_syscalls; i++){
		sys_call_table[i] = old_sys_calls[i];
	}
#endif /* 0 */
	
	printk(KERN_DEBUG "My module exit: nr_syscalls = %ld\n", nr_syscalls);    
}

module_init(my_module_init);
module_exit(my_module_exit);
    



