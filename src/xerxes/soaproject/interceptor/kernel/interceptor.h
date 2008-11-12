#ifndef _INTERCEPTOR_H
#define _INTERCEPTOR_H


#define REQUEST_SYSCALL_INTERCEPT	0
#define REQUEST_SYSCALL_RELEASE		1

#define MY_SYSCALL_NO 			0

#ifdef __KERNEL__

asmlinkage long my_syscall(int cmd, int syscall, int pid);

#define log_syscall(pid, syscall, arg1, arg2, arg3, arg4, arg5, arg6, ret) \
	printk(KERN_DEBUG"[%x]%lx(%lx,%lx,%lx,%lx,%lx,%lx)=%lx\n", pid, \
		syscall, \
		arg1, arg2, arg3, arg4, arg5, arg6, \
		ret \
	);
#endif

#endif 
