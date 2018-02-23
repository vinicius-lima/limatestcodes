import threading
from time import sleep


class Counting(threading.Thread):
    def __init__(self, count):
        threading.Thread.__init__(self)
        self.count = count

    def run(self):
        print("Counting until " + str(self.count) + ". One per second.")
        for c in range(1, self.count + 1):
            sleep(1)
            print(str(c) + "", end=' ', flush=True)
        print()


print("Creating thread...")
background = Counting(10)
background.start()

background.join()
print("Main program waited until background was done.")
