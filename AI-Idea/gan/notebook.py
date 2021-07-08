# %%

"""
This code is derived from the tensorflow tutorial:
    https://www.tensorflow.org/tutorials/generative/dcgan
"""

import glob
# import imageio
import matplotlib.pyplot as plt
import numpy as np
import os
import PIL
import tensorflow as tf
from tensorflow.keras import layers
import time

from IPython import display

from utils import *

(train_images, train_labels), (_, _) = tf.keras.datasets.mnist.load_data()

train_images = train_images.reshape(train_images.shape[0], 28, 28, 1).astype('float32')
train_images = (train_images - 127.5) / 127.5 # Normalize the images to [-1, 1]
BUFFER_SIZE = 60000
BATCH_SIZE = 256
# Batch and shuffle the data
train_dataset = tf.data.Dataset.from_tensor_slices(train_images).shuffle(BUFFER_SIZE).batch(BATCH_SIZE)

# %%

# Model

def generator_model(input_shape=(100,)):
    inp = layers.Input(shape=input_shape)
    x = layers.Dense(7 * 7 * 256, use_bias=False) (inp)
    x = layers.BatchNormalization() (x)
    x = layers.LeakyReLU() (x)

    x = layers.Reshape((7, 7, 256)) (x)

    x = deconv(x, 128, activation=None) # 14 x 14 x 128
    x = layers.BatchNormalization() (x)
    x = layers.LeakyReLU() (x)

    x = deconv(x, 1, activation='tanh') # 28 x 28 x 1
    
    model = tf.keras.Model(inp, x)
    return model

def discriminator_model(input_shape=(28, 28, 1)):
    inp = layers.Input(shape=input_shape)

    x = conv(inp, 64) # 28x28x64
    x = max_pool(x) # 14x14x64
    x = dropout(x, rate=0.3)

    x = conv(x, 128) # 14x14x128
    x = max_pool(x) # 7x7x128
    x = dropout(x, rate=0.3)

    x = layers.Flatten() (x)
    x = layers.Dense(1, activation='sigmoid') (x)

    model = tf.keras.Model(inp, x)
    return model

generator = generator_model()
discriminator = discriminator_model()

generator.summary()
discriminator.summary()

# %%
# evaluate sample

sample_noise = tf.random.normal([1, 100])
print(f"sample_noise shape: {sample_noise.shape}")
generated_image = generator(sample_noise, training=False)
print("Generated_image:")
show_img(generated_image[0])

discriminator_result = discriminator(generated_image, training=False)
print(f"Discriminator result shape: {discriminator_result.shape}")
print(f"Discriminator result val: {discriminator_result[0]}")


# %%

# Loss
loss_object = tf.keras.losses.BinaryCrossentropy()
def discriminator_loss(real, fake):
    # Real here is the outputs that classify the batch original images 
    # as real or fake
    real_output_loss = loss_object(tf.ones_like(real), real)

    # fake here is the outputs that classify the batch output of the
    # generator as real or fake
    fake_output_loss = loss_object(tf.zeros_like(fake), fake)

    return real_output_loss + fake_output_loss

def generator_loss(fake_output):
    # The generator's loss quantifies how well it was able to trick the 
    # discriminator. Intuitively, if the generator is performing well, 
    # the discriminator will classify the fake images as real (or 1). 
    # Here, we will compare the discriminators decisions on the generated 
    # images to an array of 1s.

    # fake_output here is the outputs that classify the batch output of the
    # generator as real or fake
    return loss_object(tf.ones_like(fake_output), fake_output)

# Optimizer
discriminator_optimizer = tf.keras.optimizers.Adam(learning_rate=0.0001)
generator_optimizer = tf.keras.optimizers.Adam(learning_rate=0.0001)

# %%

checkpoint_dir = './training_checkpoints'
checkpoint_prefix = os.path.join(checkpoint_dir, "ckpt")
checkpoint = tf.train.Checkpoint(
    generator_optimizer=generator_optimizer,
    discriminator_optimizer=discriminator_optimizer,
    generator=generator,
    discriminator=discriminator
)


BATCH_SIZE = 16
noise_dim = 100

@tf.function
def train_step(batch_images):
    noise = tf.random.normal([BATCH_SIZE, noise_dim])
    with tf.device('/device:gpu:0'):
        with tf.GradientTape() as gen_tape, \
                tf.GradientTape() as disc_tape: 
            generated_output = generator(noise, training=True)

            discriminator_output_real = discriminator(batch_images, training=True)
            discriminator_output_fake = discriminator(generated_output, training=True)

            disc_loss = discriminator_loss(
                discriminator_output_real,
                discriminator_output_fake
            )

            gen_loss = generator_loss(discriminator_output_fake)

        d_loss_d_gen = gen_tape.gradient(
            gen_loss, 
            generator.trainable_variables
        )
        d_loss_d_disc = disc_tape.gradient(
            disc_loss,
            discriminator.trainable_variables
        )
        generator_optimizer.apply_gradients(
            zip(
                d_loss_d_gen, 
                generator.trainable_variables
            )
        )
        discriminator_optimizer.apply_gradients(
            zip(
                d_loss_d_disc, 
                discriminator.trainable_variables
            )
        )

def display_predictions(model, input_noise, epoch, step):
    predictions = model(input_noise, training=False)
    fig = plt.figure(figsize=(10,10))
    for i in range(predictions.shape[0]):
        plt.subplot(4, 4, i + 1)
        plt.axis('off')
        plt.imshow(deprocess_img(predictions[i, :, :, 0]), cmap='gray')
        plt.savefig('./results/image_at_epoch_{:04d}_step_{:04d}.png'.format(epoch, step))
    plt.show()


# %%

# Train
EPOCHS = 50
num_examples_to_generate = BATCH_SIZE
seed = tf.random.normal([num_examples_to_generate, noise_dim])
STEP_PER_EPOCH = len(train_dataset) // BATCH_SIZE # 60000 // 16 = 

with tf.device('/device:cpu:0'):
    for epoch in range(EPOCHS):
        # start = time.time()
        
        for step, batch_images in enumerate(train_dataset):
            train_step(batch_images)
            if (step + 1) % 50 == 1:
                # Produce images for the GIF as we go
                display.clear_output(wait=True)
                print(f"Epoch {epoch + 1}, step {step + 1}:")
                display_predictions(generator, seed, epoch + 1, step + 1)

        # Save the model every 15 epochs
        if (epoch + 1) % 15 == 0:
            checkpoint.save(file_prefix = checkpoint_prefix)

    display.clear_output(wait=True)
    print(f"Epoch {epoch + 1}:")
    display_predictions(generator, seed, epoch + 1, -1)
        



# %%

# Display a single image using the epoch number
def display_image(epoch_no, step_nb):
    return PIL.Image.open('image_at_epoch_{:04d}_step_{:04d}.png'.format(epoch_no, step_nb))

anim_file = 'dcgan.gif'
import imageio
with imageio.get_writer(anim_file, mode='I') as writer:
    filenames = glob.glob('./results/image*.png')
    filenames = sorted(filenames)
    for filename in filenames:
        image = imageio.imread(filename)
        writer.append_data(image)
    image = imageio.imread(filename)
    writer.append_data(image)

import tensorflow_docs.vis.embed as embed
embed.embed_file(anim_file)

