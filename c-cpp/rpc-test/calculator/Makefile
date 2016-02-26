all: remove gen calculator client

remove:
	rm -fr calculator client

gen:
	rpcgen -N calc.x

calculator:
	gcc -o calculator calculator.c calc_svc.c calc_xdr.c -lnsl

client:
	gcc -o client client.c calc_clnt.c calc_xdr.c -lnsl

clean: remove
	rm -fr calc.h calc_clnt.c calc_svc.c calc_xdr.c

