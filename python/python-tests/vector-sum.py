if __name__ == "__main__":
    from sys import argv
    if len(argv) != 4:
        s = 'Usage:\npython ' + argv[0]
        print s, '<vector lenght> <range low limit> <range high limit>'
        exit(1)

def add_vector(vec1, vec2):
    """Add two vectors and return the result vector."""
    res = [x + y for x, y in zip(vec1, vec2)]
    return res

size = int(argv[1])
low = int(argv[2])
high = int(argv[3])

import random
random.seed()

vec1 = [random.randint(low, high) for i in range(size)]
vec2 = [random.randint(low, high) for i in range(size)]

vec3 = add_vector(vec1, vec2)

print vec1
print vec2
print vec3

# Formated print into a file

f = open('vector-sum-output.txt', 'w')
f.write('Output from vector-sum.py\n')

s = '+----' * (size + 1)
s += '+\n'
f.write(s)
f.write('|vec1|')
for i in vec1:
    s = '{0:4d}|'.format(i)
    f.write(s)
f.write('\n')

s = '+----' * (size + 1)
s += '+\n'
f.write(s)
f.write('|vec2|')
for i in vec2:
    s = '{0:4d}|'.format(i)
    f.write(s)
f.write('\n')

s = '+----' * (size + 1)
s += '+\n'
f.write(s)
f.write('|vec3|')
for i in vec3:
    s = '{0:4d}|'.format(i)
    f.write(s)
f.write('\n')

s = '+----' * (size + 1)
s += '+\n'
f.write(s)

f.close()
