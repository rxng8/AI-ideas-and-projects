from RPSEnv import RPSEnv
from RPSPlayer import *

if __name__ == '__main__':

    p1 = CFRPlayer()
    p2 = RepeatingPlayer()
    
    game = RPSEnv(p1, p2, 5)
    game.set_verbose(True)
    game.play()
    
    

