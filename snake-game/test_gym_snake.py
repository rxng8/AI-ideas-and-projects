# %%

import gym
import gymsnake
import random
env = gym.make('snake-v0')
from IPython.display import clear_output
import time
state = env.reset()

crash = False
position = None
print(str(env.x) + " " +str(env.y))
for i in range(10):
    clear_output(wait=True)
    action = random.randint(0, 2)
    position, crash = env.step(action)
    if crash:
        break
    print(str(env.x) + " " +str(env.y))
    env.render()
    time.sleep(0.1)
    