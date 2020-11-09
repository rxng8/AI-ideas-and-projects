
import random
from keras.utils import to_categorical
import numpy as np
from .Food import Food

class Player(object):
    def __init__(self, game):
        self.x = int(0.5 * game.game_width)
        self.y = int(0.5 * game.game_height)
        # self.x = x - x % 20
        # self.y = y - y % 20
        self.position = []
        self.position.append([self.x, self.y])
        # Length of the snake body!
        self.food = 1
        self.eaten = False
        self.x_change = 1
        self.y_change = 0

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
    def do_move(self, move, x, y, game, food):
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
          or self.x >= game.game_width \
          or self.y < 0 \
          or self.y >= game.game_height \
          or [self.x, self.y] in self.position:
            game.crash = True

        Food.eat(self, food, game)

        self.update_position(self.x, self.y)

    def display_player(self, x, y, food, game):
        # self.position[-1][0] = x
        # self.position[-1][1] = y

        # if game.crash == False:
        #     for i in range(food):
        #         x_temp, y_temp = self.position[len(self.position) - 1 - i]
        #         game.gameDisplay.blit(self.image, (x_temp, y_temp))

        #     update_screen()
        # else:
        #     pygame.time.wait(300)
        # Tentative: string!

        pass