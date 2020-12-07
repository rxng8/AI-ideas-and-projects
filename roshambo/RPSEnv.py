
from abc import ABC
from RPSPlayer import RPSPlayer

class RPSEnv(): 
    """RPS OpenAI Gym Environment

    - Non-terminal states: Player 1 turn totals less than n, state n indicates a terminal state (win/loss)
    - Actions: 0 (Rock), 1 (Paper), 2 (Scissor)
    - Rewards: +1 for transition to terminal state with win 
               -1 for transition to terminal state with loss
    - Transitions: transition in each move.
    - Terminal state: After complete k-th number of games
    """
    
    metadata = {'render.modes': ['human', 'ansi']}
    reward_range = (float('0'), float('1'))
    
    ROCK = 0
    PAPER = 1
    SCISSOR = 2

    VERBOSE = False

    ACTION_MAP = {0: 'ROCK', 1: 'PAPER', 2: 'SCISSOR'}

    def __init__(self, p1: RPSPlayer, p2: RPSPlayer, n_turns: int=10):
        self.n_turns = n_turns
        self.turn = 0
        self.p1 = p1
        self.p2 = p2
        self.points = [0, 0]
        self.done = False

    def reset(self):
        self.turn = 0
        self.done = False
        self.points = [0, 0]

    def play(self):
        self.reset()
        print("Playing...")
        while(self.turn < self.n_turns):
            if self.VERBOSE:
                print("Turn " + str(self.turn) + ":")
            a1 = self.p1.step()
            a2 = self.p2.step()
            p1_turn_win = False
            if self.VERBOSE:
                print("Player 1 plays " + self.ACTION_MAP[a1])
                print("Player 2 plays " + self.ACTION_MAP[a2])
            if a1 - 1 >= 0:
                if a1 - 1 == a2:
                    p1_turn_win = True
            else:
                if a1 + 2 == a2:
                    p1_turn_win = True
            if self.VERBOSE:
                print("Player 1 wins!") if p1_turn_win else \
                    print("Player 2 wins!")

            if p1_turn_win:
                self.points[0] += 1
                self.points[1] -= 1
            else:
                self.points[0] -= 1
                self.points[1] += 1
            
            self.p1.report(self.turn, a2, 1 if p1_turn_win else -1)
            self.p2.report(self.turn, a1, -1 if p1_turn_win else 1)
            self.turn += 1

        [point1, point2] = self.points
        self.p1.terminal(point1 > point2)
        self.p2.terminal(point1 < point2)

        if point1 == point2:
            print(f"Draw! Total point: {str(self.points)}")
        elif(point1 > point2):
            print(f"Player 1 won! Total point: {str(self.points)}")
        else:
            print(f"Player 2 won! Total point: {str(self.points)}")
        
    def set_verbose(self, b: bool):
        self.VERBOSE = b

    def __str__(self):
        pass

"""
class RPSEnvKaggle:
    import json
    from os import path
    from .agents import agents as all_agents
    from .utils import get_score


    def interpreter(state, env):
        player1 = state[0]
        player2 = state[1]

        # Specification can fully handle the reset.
        if env.done:
            return state

        step = len(env.steps)
        player1.observation.step = step

        def is_valid_action(player, sign_count):
            return (
                player.action is not None and
                isinstance(player.action, int) and
                0 <= player.action < sign_count
            )

        # Check for validity of actions
        is_player1_valid = is_valid_action(player1, env.configuration.signs)
        is_player2_valid = is_valid_action(player2, env.configuration.signs)
        if not is_player2_valid:
            player2.status = "INVALID"
            player2.reward = 0

            if is_player1_valid:
                player1.status = "DONE"
                player1.reward = 1
                return state

        if not is_player1_valid:
            player1.status = "INVALID"
            player1.reward = 0

            if is_player2_valid:
                player2.status = "DONE"
                player2.reward = 1
                return state
            else:
                return state

        score = get_score(player1.action, player2.action)
        player1.observation.lastOpponentAction = player2.action
        player1.reward += score
        player2.observation.lastOpponentAction = player1.action
        player2.reward += -score
        remaining_steps = env.configuration.episodeSteps - step - 1
        if abs(player1.reward - player2.reward) > remaining_steps * 2:
            player1.status = "DONE"
            player2.status = "DONE"
        elif remaining_steps <= 0:
            player1.status = "DONE"
            player2.status = "DONE"
        return state


    def renderer(state, env):
        sign_names = ["Rock", "Paper", "Scissors", "Spock", "Lizard"]
        rounds_played = len(env.steps)
        board = ""

        # This line prints results each round, good for debugging
        for i in range(1, rounds_played):
            step = env.steps[i]
            right_move = step[0].observation.lastOpponentAction
            left_move = step[1].observation.lastOpponentAction
            board += f"Round {i}: {sign_names[left_move]} vs {sign_names[right_move]}, Score: {step[0].reward} to {step[1].reward}\n"

        board += f"Game ended on round {rounds_played - 1}, final score: {state[0].reward} to {state[0].reward}\n"
        return board


    dir_path = path.dirname(__file__)
    json_path = path.abspath(path.join(dir_path, "rps.json"))
    with open(json_path) as json_file:
        specification = json.load(json_file)


    def html_renderer():
        js_path = path.abspath(path.join(dir_path, "rps.js"))
        with open(js_path, encoding="utf-8") as js_file:
            return js_file.read()


    agents = all_agents

"""