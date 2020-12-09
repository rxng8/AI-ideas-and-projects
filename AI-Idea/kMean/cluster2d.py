import sys
import matplotlib.pyplot as plt

def cluster2d(filename):
    with open(filename) as f:
        content = f.readlines()
        data = [list(map(float, x.strip().split(' '))) for x in content if not x.startswith('%')]
        c = [int(x[0]) for x in data]
        x = [x[1] for x in data]
        y = [x[2] for x in data]
        plt.scatter(x, y, c=c, marker='+')
        plt.axis([0, 1, 0, 1])
        plt.show()

if (len(sys.argv) > 1):  # Use first command line argument
    cluster2d(sys.argv[1])

# Alternatively, comment previous and uncomment and manually edit next line:
# cluster2d('easygaussian4-soln.txt')