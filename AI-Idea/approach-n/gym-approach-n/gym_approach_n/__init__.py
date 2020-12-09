from gym.envs.registration import register

register(
    id='approach-n-v1',
    entry_point='gym_approach_n.envs:ApproachNEnv',
)
