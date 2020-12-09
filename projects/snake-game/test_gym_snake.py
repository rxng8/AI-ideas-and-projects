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

def euclidean_distance(x1, y1, x2, y2):
    return (x1 - x2) ** 2 + (y1 - y2) ** 2

def heuristic1(position, x_food, y_food, crash, eaten):
    # Compute rewards!
    head = position[-1]
    # Head is guaranteed to be not None
    reward = -euclidean_distance(head[0], head[1], x_food, y_food) + (100 if eaten else 0)
    return reward

d = {'UP': 0, 'DOWN': 1, 'LEFT': 2, 'RIGHT': 3}
db = {0: 'UP', 1: 'DOWN', 2: 'LEFT', 3: 'RIGHT'}
dtoa = {0: 'GOING FORWARD', 1: 'STEER LEFT', 2: 'STEER RIGHT'}
def get_state(head_x, head_y, food_x, food_y):
    # Food is farther or smaller in vertical dimension than 
    #   horizontal dimension
    dif = abs (head_x - food_x) - abs(head_y - food_y)
    # if food is farther or smaller in vertical dimension then priotitizing state x
    if dif >= 0:
        if food_x >= head_x:
            return d['RIGHT']
        elif food_x < head_x:
            return d['LEFT']
    else:
        if food_y >= head_y:
            return d['DOWN']
        elif food_y < head_y:
            return d['UP']
    return -1

def check_hitting_body(position: List[List[int]], next_direction: int):
    if len(position) < 3:
        return False
    head = position[-1]
    head_x = head[0]
    head_y = head[1]
    next_head_x = head_x
    next_head_y = head_y
    if next_direction == d['UP']:
        next_head_x -= 1
    elif next_direction == d['DOWN']:
        next_head_x += 1
    elif next_direction == d['LEFT']:
        next_head_y -= 1
    elif next_direction == d['RIGHT']:
        next_head_y += 1
    return next_head_x in position[1:][0] and next_head_y in position[1:][1]

def get_next_direction(policies, position):
    p = policies.copy()
    direction = int(np.argmax(p[state,:]))
    while check_hitting_body(position, direction):
        p[:, direction] = -1e8
        direction = int(np.argmax(p[state,:]))
    return direction

def get_action(position, direction, head_x, head_y, prev_x, prev_y):
    # If this is none then the direction is default to goiing right!
    if prev_x == None or prev_y == None:
        if direction == d['UP'] and not check_hitting_body(position, direction):
            return 1
        if direction == d['DOWN'] and not check_hitting_body(position, direction):
            return 2
        if direction == d['RIGHT'] and not check_hitting_body(position, direction):
            return 0
        if direction == d['LEFT'] and not check_hitting_body(position, direction):
            # return -1
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
                return random.randint(1, 2)
        # Going left
        elif head_y < prev_y:
            if direction == d['UP']:
                return 2
            if direction == d['DOWN']:
                return 1
            if direction == d['RIGHT']:
                # return -1
                return random.randint(1, 2)
            if direction == d['LEFT']:
                return 0
    elif head_y == prev_y:
        # Going down
        if head_x > prev_x:
            if direction == d['UP']:
                # return -1
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
                return random.randint(1, 2)
            if direction == d['RIGHT']:
                return 2
            if direction == d['LEFT']:
                return 1
    # There is case that the snake have nowhere to go but to hit itself! Hopelessly going forward!
    return 0

# def check_valid_action(action, head_x, head_y):
#     pass

# print(str(env.x) + " " +str(env.y))

policies = np.asarray([[0] * 4] * 4)
# %%
learning_rate = 0.1
discount_rate = 0.99

exploration_rate = 0.1
# max_exploration_rate = 1
# min_exploration_rate = 0.1
# exploration_decay_rate = 0.001

num_episodes = 1000
max_steps_per_episode = 10000
for episode in range(num_episodes):

    clear_output(wait=False)
    print("Current Epoch: " + str(episode))

    position, x_food, y_food, crash = env.reset()
    head = position[-1]
    prev_head_x = None
    prev_head_y = None
    
    state = get_state(head[0], head[1], x_food, y_food)
    for i in range(max_steps_per_episode):
        clear_output(wait=True)

        
        # action = random.randint(0, 2)



        # Exploration-exploitation trade-off
        exploration_rate_threshold = random.uniform(0, 1)
        if exploration_rate_threshold < exploration_rate:
            direction = random.randint(0, 2)
            action = direction
        else:
            # direction = int(np.argmax(policies[state,:]))
            direction = get_next_direction(policies, position)
            head = position[-1]
            action = get_action(position, direction, head[0], head[1],
                            position[-2][0] if len(position) > 1 else None,
                            position[-2][1] if len(position) > 1 else None)
            

        

        """
            position (List[List]): The position of the snake body. 
                Shape: (body length, 2). with 2 is the x and y coordinates.
                The head is always at the last position of the list
            x_food (int): x coord of the food
            y_food (int): y coord of the food
            crash (bool): Whether the game ends.
        """
        # action_str = input()

        # if action_str == 'w':
        #     direction = d['UP']
        # elif action_str == 'a':
        #     direction = d['LEFT']
        # elif action_str == 'd':
        #     direction = d['RIGHT']
        # elif action_str == 's':
        #     direction = d['DOWN']
        # action = get_action(position, direction, head[0], head[1], prev_head_x, prev_head_y)

        prev_head_x = head[0]
        prev_head_y = head[1]
        position, new_x_food, new_y_food, reward, crash = env.step(action)
        head = position[-1]
        eaten: bool = head[0] == x_food and head[1] == y_food
        # reward = heuristic1(position, new_x_food, new_y_food, crash, eaten)
        
        new_state = get_state(head[0], head[1], new_x_food, new_y_food)

        # Update Q-table
        policies[state, direction] = policies[state, direction] * (1 - learning_rate) + \
        learning_rate * (reward + discount_rate * np.max(policies[new_state, :]))
        
        state = new_state
        x_food = new_x_food
        y_food = new_y_food

        if crash:
            env.reset()
            crash = False
            break
        print("Previous position: " + str(prev_head_x) + " " +str(prev_head_x))
        print("Current position: " + str(env.x) + " " +str(env.y))
        print("ACTION: " + dtoa[action])
        print("Direction of action: " + str(db[direction]))
        print("Reward of the current state: " + str(reward))
        print("Index: " + str(db))
        print("Current Q-table: \n" + str(policies))
        env.render()
        time.sleep(1)
    

# %%

print(policies)

# %%
# PLAY
GAME = 100
MAX_STEP_GAME = 10000
for episode in range(GAME):
    position, x_food, y_food, crash = env.reset()
    head = position[-1]
    prev_head_x = None
    prev_head_y = None

    state = get_state(head[0], head[1], x_food, y_food)
    for i in range(MAX_STEP_GAME):
        

        clear_output(wait=True)
        # action = random.randint(0, 2)

        # Exploration-exploitation trade-off
        exploration_rate_threshold = random.uniform(0, 1)
        if exploration_rate_threshold > exploration_rate:
            # direction = int(np.argmax(policies[state,:]))
            direction = get_next_direction(policies, position)
            head = position[-1]
            action = get_action(position, direction, head[0], head[1],
                            position[-2][0] if len(position) > 1 else None,
                            position[-2][1] if len(position) > 1 else None)
        else:
            direction = random.randint(0, 2)
            action = direction

        # action_str = input()

        # if action_str == 'w':
        #     direction = d['UP']
        # elif action_str == 'a':
        #     direction = d['LEFT']
        # elif action_str == 'd':
        #     direction = d['RIGHT']
        # elif action_str == 's':
        #     direction = d['DOWN']
        # action = get_action(position, direction, head[0], head[1], prev_head_x, prev_head_y)

        prev_head_x = head[0]
        prev_head_y = head[1]
        position, new_x_food, new_y_food, crash = env.step(action)
        head = position[-1]
        eaten: bool = head[0] == x_food and head[1] == y_food
        reward = heuristic1(position, new_x_food, new_y_food, crash, eaten)
        
        new_state = get_state(head[0], head[1], new_x_food, new_y_food)

        # Update Q-table
        policies[state, direction] = policies[state, direction] * (1 - learning_rate) + \
        learning_rate * (reward + discount_rate * np.max(policies[new_state, :]))
        
        state = new_state
        x_food = new_x_food
        y_food = new_y_food

        if crash:
            env.reset()
            crash = False
            break
        print("Current position: " + str(env.x) + " " +str(env.y))
        print("ACTION: " + dtoa[action])
        print("Direction of action: " + str(db[direction]))
        env.render()
        time.sleep(0.05)

# %%

# from keras.utils import to_categorical
# to_categorical(0)
