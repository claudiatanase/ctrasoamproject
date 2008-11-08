KDIR=/lib/modules/`uname -r`/build

all: kbuild

kbuild:
	make -C $(KDIR) M=`pwd`

clean:
	make -C $(KDIR) M=`pwd` clean
       
