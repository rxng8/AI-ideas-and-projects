import sys
import matplotlib.pyplot as plt

def data2d(filename):
    with open(filename) as f:
        content = f.readlines()
        data = [list(map(float, x.strip().split(' '))) for x in content if not x.startswith('%')]
        x, y = map(list, zip(*data))
        plt.scatter(x, y, marker='+')
        plt.axis([0, 1, 0, 1])
        plt.show()

if (len(sys.argv) > 1):  # Use first command line argument
    data2d(sys.argv[1])

# Alternatively, comment previous and uncomment and manually edit next line:
# data2d('easygaussian4.dat')
