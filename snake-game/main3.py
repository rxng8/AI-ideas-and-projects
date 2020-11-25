# %%

"""

    Third main file with 16 states

"""



# %%
from typing import List, Dict
import gym
import gymsnake
import random
import numpy as np
env = gym.make('snake-v0')
from IPython.display import clear_output
import time
import math
state = env.reset()

crash = False
position = None
d = {'UP': 0, 'DOWN': 1, 'LEFT': 2, 'RIGHT': 3}

# Prefix o is the obstacle
st = {'UPo': 0, 'UP_RIGHTo': 1, 'RIGHTo': 2, 'DOWN_RIGHTo': 3, 
'DOWNo': 4, 'DOWN_LEFTo': 5, 'LEFTo': 6, 'UP_LEFTo': 7,
'UP': 8, 'UP_RIGHT': 9, 'RIGHT': 10, 'DOWN_RIGHT': 11, 
'DOWN': 12, 'DOWN_LEFT': 13, 'LEFT': 14, 'UP_LEFT': 15}

db = {0: 'UP', 1: 'DOWN', 2: 'LEFT', 3: 'RIGHT'}
dtoa = {0: 'GOING FORWARD', 1: 'STEER LEFT', 2: 'STEER RIGHT'}

policies = np.asarray([[0.0] * 4] * 16)

learning_rate = 0.3
discount_rate = 0.99
exploration_rate = 0.3

num_episodes = 1000
max_steps_per_episode = 10000

def get_state(position, head_x, head_y, food_x, food_y):
    # Food is farther or smaller in vertical dimension than 
    #   horizontal dimension
    dif_x = abs (head_x - food_x)
    dif_y = abs(head_y - food_y)
    key_string = ''
    # On the same row, either right or left
    if dif_x == 0 and dif_y != 0:
        if food_y > head_y:
            key_string = 'RIGHT'
        elif food_y < head_y:
            key_string = 'LEFT'
    # Vertical dimension
    elif dif_x != 0 and dif_y == 0:
        if food_x > head_x:
            key_string = 'DOWN'
        elif food_x < head_x:
            key_string = 'UP'
        pass
    # On diagonal
    elif dif_x != 0 and dif_y != 0:
        if food_x > head_x and food_y > head_y:
            key_string = 'DOWN_RIGHT'
        elif food_x > head_x and food_y < head_y:
            key_string = 'DOWN_LEFT'
        elif food_x < head_x and food_y > head_y:
            key_string = 'UP_RIGHT'
        elif food_x < head_x and food_y < head_y:
            key_string = 'UP_LEFT'
    if has_obstacle(position, head_x, head_y, food_x, food_y):
        key_string += 'o'
    
    # Trivial error handling
    if key_string == '' or key_string == 'o':
        return 0
    return st[key_string]

# Check if there is a snake body between the snake head and the food.
def has_obstacle(position, head_x, head_y, food_x, food_y):
    snake_body = position.copy()[:-1]
    for body in snake_body:
        if body[0] == head_x and abs(body[1] - head_y) < abs(food_y - head_y) \
            or body[1] == head_y and abs(body[0] - head_x) < abs(food_x - head_x):
            return True
    return False

def check_valid_move_direction(direction, head_x, head_y, prev_x, prev_y) -> bool:
    # print("Proposed direction: " + db[direction])

    # Default is going right
    if prev_x == None or prev_y == None:
        return direction != d['LEFT']
    if head_x == prev_x:
        # Going right
        if head_y > prev_y:
            return direction != d['LEFT']
        # Going left
        elif head_y < prev_y:
            return direction != d['RIGHT']
    elif head_y == prev_y:
        # Going down
        if head_x > prev_x:
            return direction != d['UP']
        # Going up
        elif head_x < prev_x:
           return direction != d['DOWN']
    return True

def get_direction(policies, head_x, head_y, prev_x, prev_y, random=False):
    if random:
        direction = np.random.randint(0,3)
        while not check_valid_move_direction(direction, head_x, head_y, prev_x, prev_y):
            direction = np.random.randint(0,3)
        return direction
    else:
        p = policies.copy()
        direction = int(np.argmax(p[state,:]))
        while not check_valid_move_direction(direction, head_x, head_y, prev_x, prev_y):
            p[:, direction] = -1e8
            direction = int(np.argmax(p[state,:]))
        return direction

def get_action(position, direction, head_x, head_y, prev_x, prev_y):
    # If this is none then the direction is default to goiing right!
    if prev_x == None or prev_y == None:
        if direction == d['UP']:
            return 1
        if direction == d['DOWN']:
            return 2
        if direction == d['RIGHT']:
            return 0
        if direction == d['LEFT']:
            # return -1
            print("invalid behavior!")
            return random.randint(1, 2)

    if head_x == prev_x:
        # Going right
        if head_y > prev_y:
            if direction == d['UP']:
                return 1
            if direction == d['DOWN']:
                return 2
            if direction == d['RIGHT']:
                return 0
            if direction == d['LEFT']:
                # return -1
                print("invalid behavior!")
                return random.randint(1, 2)
        # Going left
        elif head_y < prev_y:
            if direction == d['UP']:
                return 2
            if direction == d['DOWN']:
                return 1
            if direction == d['RIGHT']:
                # return -1
                print("invalid behavior!")
                return random.randint(1, 2)
            if direction == d['LEFT']:
                return 0
    elif head_y == prev_y:
        # Going down
        if head_x > prev_x:
            if direction == d['UP']:
                # return -1
                print("invalid behavior!")
                return random.randint(1, 2)
            if direction == d['DOWN']:
                return 0
            if direction == d['RIGHT']:
                return 1
            if direction == d['LEFT']:
                return 2
        # Going up
        elif head_x < prev_x:
            if direction == d['UP']:
                return 0
            if direction == d['DOWN']:
                # return -1
                print("invalid behavior!")
                return random.randint(1, 2)
            if direction == d['RIGHT']:
                return 2
            if direction == d['LEFT']:
                return 1
    # There is case that the snake have nowhere to go but to hit itself! Hopelessly going forward!
    return 0

# def get_next_direction(policies, position):
#     p = policies.copy()
#     direction = int(np.argmax(p[state,:]))
#     while check_hitting_body(position, direction):
#         p[:, direction] = -1e8
#         direction = int(np.argmax(p[state,:]))
#     return direction

# %%


for episode in range(num_episodes):

    clear_output(wait=True)
    
    position, x_food, y_food, crash = env.reset()
    head = position[-1]
    prev_head_x = None
    prev_head_y = None
    state = get_state(position, head[0], head[1], x_food, y_food)

    for i in range(max_steps_per_episode):
        
        clear_output(wait=True)
        print("Current Epoch: " + str(episode))

        # Exploration-exploitation trade-off
        expl = random.uniform(0, 1)

        if expl < exploration_rate:
            print("This turn play random action!")
            direction = get_direction(None, head[0], head[1], prev_head_x, prev_head_y, random=True)
        else:
            print("This turn play according to the Q-table!")
            direction = get_direction(policies, head[0], head[1], prev_head_x, prev_head_y)
        action = get_action(position, direction, head[0], head[1], prev_head_x, prev_head_y)
        
        prev_head_x = head[0]
        prev_head_y = head[1]

        """
            position (List[List]): The position of the snake body. 
                Shape: (body length, 2). with 2 is the x and y coordinates.
                The head is always at the last position of the list
            x_food (int): x coord of the food
            y_food (int): y coord of the food
            crash (bool): Whether the game ends.
        """
        position, new_x_food, new_y_food, reward, crash = env.step(action)
        head = position[-1]
        eaten: bool = head[0] == x_food and head[1] == y_food
        # reward = heuristic1(position, new_x_food, new_y_food, crash, eaten)
        # reward = 0
        new_state = get_state(position, head[0], head[1], new_x_food, new_y_food)

        # Update Q-table
        policies[state, direction] = policies[state, direction] * (1 - learning_rate) + \
        learning_rate * (reward + discount_rate * np.max(policies[new_state, :]))
        
        state = new_state
        x_food = new_x_food
        y_food = new_y_food
        

        # print("Previous position: " + str(prev_head_x) + " " +str(prev_head_x))
        print("Current position: " + str(env.x) + " " +str(env.y))
        print("ACTION: " + dtoa[action])
        # print("Direction of action: " + str(db[direction]))
        print("Reward of the current state: " + str(reward))
        print("Index: " + str(st))
        print("Current Q-table: \n" + str(policies))
        env.render()


        if crash:
            env.reset()
            crash = False
            break

        
        time.sleep(0.05)
    time.sleep(0.1)
