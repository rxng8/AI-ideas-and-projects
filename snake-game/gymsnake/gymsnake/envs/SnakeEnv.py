import gym
from gym import error, spaces, utils
from gym.utils import seeding
import random
from keras.utils import to_categorical
import numpy as np
from io import StringIO
from contextlib import closing
import sys



class SnakeEnv(gym.Env):
    metadata = {'render.modes': ['human']}

    def __init__(self):
        self.game_width = 10
        self.game_height = 10
        self.crash = False
        # self.player = Player(self)
        # self.food = Food()
        self.score = 0
        # action = [1, 0, 0]
        # move = to_categorical(action, num_classes=3)
        # self.player.do_move(move, player.x, player.y, self, self.player.food)
        
        # From player class
        self.x = int(0.5 * self.game_width)
        self.y = int(0.5 * self.game_height)
        # self.x = x - x % 20
        # self.y = y - y % 20
        self.position = []
        self.position.append([self.x, self.y])
        # Length of the snake body!
        self.food = 1
        self.eaten = False
        self.x_change = 1
        self.y_change = 0

        # From food class
        self.x_food = 3
        self.y_food = 3



    def step(self, action):
        reward = 0
        move = to_categorical(action, num_classes=3)
        self.do_move(move, self.x, self.y, self.food)
        # return observation, state, reward, configuration
        return self.position, self.crash

    def reset(self):
        self.crash = False
        self.score = 0
        self.food = 1

    def render(self, mode='human', close=False):
        string = ""
        # print(self.position[:][0])
        print(self.position[:])
        for i in range(self.game_height):
          for j in range(self.game_width):
            # print(str(i) + " " + str(j))
            bx = False
            for x in range(len(self.position)):
              if i == self.position[x][0]:
                bx = True

            by = False
            for y in range(len(self.position)):
              if j == self.position[y][1]:
                by = True

            if bx and by:
              string += "#"
            # if self.position and (i in self.position[:][0] and j in self.position[:][1]):
            #   string += "#"
            elif i == self.x_food and j == self.y_food:
              string += "o"
            else:
              string += "_"
          string += "\n"
        print(string)


        # outfile = StringIO() if mode == 'ansi' else sys.stdout

        # outfile.write(string)

        # if mode != 'human':
        #     with closing(outfile):
        #         return outfile.getvalue()





    def update_position(self, x, y):
        # Head position x != x or head position y != y
        if self.position[-1][0] != x or self.position[-1][1] != y:

            # Shift the whole body by 1 position in position array!
            if self.food > 1:
                for i in range(0, self.food - 1):
                    self.position[i][0], self.position[i][1] = self.position[i + 1]

            # Shift the last position!
            self.position[-1][0] = x
            self.position[-1][1] = y
    
    # def do_move(self, move, x, y, game, food, agent):
    def do_move(self, move, x, y, food):
        move_array = [self.x_change, self.y_change]

        if self.eaten:
            self.position.append([self.x, self.y])
            self.eaten = False
            self.food = self.food + 1
        if np.array_equal(move, [1, 0, 0]):
            move_array = self.x_change, self.y_change
        elif np.array_equal(move, [0, 1, 0]) and self.y_change == 0:  # right - going horizontal
            move_array = [0, self.x_change]
        elif np.array_equal(move, [0, 1, 0]) and self.x_change == 0:  # right - going vertical
            move_array = [-self.y_change, 0]
        elif np.array_equal(move, [0, 0, 1]) and self.y_change == 0:  # left - going horizontal
            move_array = [0, -self.x_change]
        elif np.array_equal(move, [0, 0, 1]) and self.x_change == 0:  # left - going vertical
            move_array = [self.y_change, 0]
        # The new xchange y change! 
        self.x_change, self.y_change = move_array
        self.x = x + self.x_change
        self.y = y + self.y_change

         # Crash condition!
        if self.x < 0 \
          or self.x >= self.game_width \
          or self.y < 0 \
          or self.y >= self.game_height \
          or [self.x, self.y] in self.position:
            self.crash = True

        self.eat()

        self.update_position(self.x, self.y)



    def food_coord(self):
        # Random position spawn of food!
        x_rand = random.randint(0, self.game_width - 1)
        # Normalize
        # self.x_food = x_rand - x_rand % 20
        self.x_food = x_rand
        y_rand = random.randint(0, self.game_height - 1)
        # Normalize
        # self.y_food = y_rand - y_rand % 20
        self.y_food = y_rand

        # While the spawn is in snake body, recursive!
        if [self.x_food, self.y_food] not in self.position:
            return self.x_food, self.y_food
        else:
            self.food_coord()


    def eat(self):
        if self.x == self.x_food and self.y == self.y_food:
            self.food_coord()
            self.eaten = True
            self.score = self.score + 1