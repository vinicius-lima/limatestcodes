def make_incrementor(n):
    return lambda x: x + n

x = int(raw_input("Please enter an integer: "))

f = make_incrementor(x)
print f(0)
print f(1)
