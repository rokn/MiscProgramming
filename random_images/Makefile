CC=gcc
CFLAGS=-O3 -Wall -fopenmp
LDFLAGS=-O2 -Wall -fopenmp

random_images: main.o image.o
	${CC} ${LDFLAGS} main.o image.o -o random_images -lm

main.o: main.c image.h
	${CC} ${CFLAGS} -c main.c

image.o: image.c image.h
	${CC} ${CFLAGS} -c image.c

clean:
	rm -f *.o random_images out.bmp
