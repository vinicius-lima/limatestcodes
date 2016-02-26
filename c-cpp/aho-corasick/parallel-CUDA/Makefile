all: clean pattern-machine

clean:
	rm -fr pattern-machine

pattern-machine:
	nvcc -o pattern-machine tools.cu queue.cu aho-corasick.cu main.cu

