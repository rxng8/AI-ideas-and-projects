"""
    A library to train and play snake env

    @author: Alex Nguyen
"""
import sys
import argparse



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Process some integers.')
    parser.add_argument('integers', metavar='N', type=int, nargs='+',
                    help='an integer for the accumulator')
    parser.add_argument('--sum', dest='accumulate', action='store_const',
                    const=sum, default=max,
                    help='sum the integers (default: find the max)')
    args = parser.parse_args()
    print(args.accumulate(args.integers))

    # Params: config? (dqn config, q learning config, detyerministic?), path to saved model if deterministic.
    