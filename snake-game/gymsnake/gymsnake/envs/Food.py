
import random
from keras.utils import to_categorical
import numpy as np

class Food(object):
    def __init__(self):
        self.x_food = 3
        self.y_food = 3
        # self.image = pygame.image.load('img/food2.png')
        self.image = 'o'

    def food_coord(self, game, player):
        # Random position spawn of food!
        x_rand = random.randint(0, game.game_width - 1)
        # Normalize
        # self.x_food = x_rand - x_rand % 20
        self.x_food = x_rand
        y_rand = random.randint(0, game.game_height - 1)
        # Normalize
        # self.y_food = y_rand - y_rand % 20
        self.y_food = y_rand

        # While the spawn is in snake body, recursive!
        if [self.x_food, self.y_food] not in player.position:
            return self.x_food, self.y_food
        else:
            self.food_coord(game, player)

    def display_food(self, x, y, game):
        # game.gameDisplay.blit(self.image, (x, y))
        # update_screen()

        # tantative: __str__(): return string!
        pass

    @staticmethod
    def eat(player, food, game):
        if player.x == food.x_food and player.y == food.y_food:
            food.food_coord(game, player)
            player.eaten = True
            game.score = game.score + 1