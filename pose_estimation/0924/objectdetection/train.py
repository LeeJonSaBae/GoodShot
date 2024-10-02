# %%shell
# eval "$(conda shell.bash hook)"
# conda activate myenv

import numpy as np
import os

np.object = object
np.bool = bool
np.complex = complex

from tflite_model_maker import image_classifier

from tflite_model_maker.config import QuantizationConfig
from tflite_model_maker.config import ExportFormat
from tflite_model_maker import model_spec
from tflite_model_maker import object_detector

import tensorflow as tf
assert tf.__version__.startswith('2')

tf.get_logger().setLevel('ERROR')
from absl import logging
logging.set_verbosity(logging.ERROR)

use_custom_dataset = True
dataset_is_split = True

if use_custom_dataset:

  # The ZIP file you uploaded:
  #!unzip dataset.zip

  # Your labels map as a dictionary (zero is reserved):
#   label_map = {0:'background', 1:'club', 2:'club_head', 3:'golf_hole', 4:'golf_mat', 5:'golfball', 6:'person', 7:'player_not_ready', 8:'player_ready'}

  label_map = {
    0: 'background', 
    1: 'club', 
    2: 'club_head', 
    3: 'golf_hole', 
    4: 'golf_mat', 
    5: 'golfball', 
    6: 'person', 
    7: 'player_not_ready', 
    8: 'player_ready',
    9: 'golf club-handle',
    10: 'golf club-head',
    11: 'golf',
    12: 'golf ball'
  }

  # label_map_dict = {
  #         'background': 0, 
  #         'club': 1, 
  #         'club_head': 2, 
  #         'golf_hole': 3, 
  #         'golf_mat': 4, 
  #         'golfball': 5, 
  #         'person': 6, 
  #         'player_not_ready': 7, 
  #         'player_ready': 8,
  #         'golf club-handle': 9,
  #         'golf club-head': 10,
  #         'golf': 11,
  #         'golf ball': 12
  #     }

  print(label_map)

  classes = []
  
#   try:
#     class_label = label_map[int(obj['name'])]  # Convert to int for key lookup
#     classes.append(class_label)
#   except KeyError:
#     print(f"Warning: Class label '{obj['name']}' not found in label map.")

  if dataset_is_split:
    # If your dataset is already split, specify each path:
    train_images_dir = 'dataset/train/images'
    train_annotations_dir = 'dataset/train/annotations'
    val_images_dir = 'dataset/validation/images'
    val_annotations_dir = 'dataset/validation/annotations'
    test_images_dir = 'dataset/test/images'
    test_annotations_dir = 'dataset/test/annotations'
  else:
    # If it's NOT split yet, specify the path to all images and annotations
    images_in = 'dataset/images'
    annotations_in = 'dataset/annotations'

import os
import random
import shutil

def split_dataset(images_path, annotations_path, val_split, test_split, out_path):
  """Splits a directory of sorted images/annotations into training, validation, and test sets.

  Args:
    images_path: Path to the directory with your images (JPGs).
    annotations_path: Path to a directory with your VOC XML annotation files,
      with filenames corresponding to image filenames. This may be the same path
      used for images_path.
    val_split: Fraction of data to reserve for validation (float between 0 and 1).
    test_split: Fraction of data to reserve for test (float between 0 and 1).
  Returns:
    The paths for the split images/annotations (train_dir, val_dir, test_dir)
  """
  _, dirs, _ = next(os.walk(images_path))

  train_dir = os.path.join(out_path, 'train')
  val_dir = os.path.join(out_path, 'validation')
  test_dir = os.path.join(out_path, 'test')

  IMAGES_TRAIN_DIR = os.path.join(train_dir, 'images')
  IMAGES_VAL_DIR = os.path.join(val_dir, 'images')
  IMAGES_TEST_DIR = os.path.join(test_dir, 'images')
  os.makedirs(IMAGES_TRAIN_DIR, exist_ok=True)
  os.makedirs(IMAGES_VAL_DIR, exist_ok=True)
  os.makedirs(IMAGES_TEST_DIR, exist_ok=True)

  ANNOT_TRAIN_DIR = os.path.join(train_dir, 'annotations')
  ANNOT_VAL_DIR = os.path.join(val_dir, 'annotations')
  ANNOT_TEST_DIR = os.path.join(test_dir, 'annotations')
  os.makedirs(ANNOT_TRAIN_DIR, exist_ok=True)
  os.makedirs(ANNOT_VAL_DIR, exist_ok=True)
  os.makedirs(ANNOT_TEST_DIR, exist_ok=True)

  # Get all filenames for this dir, filtered by filetype
  filenames = os.listdir(os.path.join(images_path))
  filenames = [os.path.join(images_path, f) for f in filenames if (f.endswith('.jpg'))]
  # Shuffle the files, deterministically
  filenames.sort()
  random.seed(42)
  random.shuffle(filenames)
  # Get exact number of images for validation and test; the rest is for training
  val_count = int(len(filenames) * val_split)
  test_count = int(len(filenames) * test_split)
  for i, file in enumerate(filenames):
    source_dir, filename = os.path.split(file)
    annot_file = os.path.join(annotations_path, filename.replace(".jpg", ".xml"))
    if i < val_count:
      shutil.copy(file, IMAGES_VAL_DIR)
      shutil.copy(annot_file, ANNOT_VAL_DIR)
    elif i < val_count + test_count:
      shutil.copy(file, IMAGES_TEST_DIR)
      shutil.copy(annot_file, ANNOT_TEST_DIR)
    else:
      shutil.copy(file, IMAGES_TRAIN_DIR)
      shutil.copy(annot_file, ANNOT_TRAIN_DIR)
  return (train_dir, val_dir, test_dir)

# We need to instantiate a separate DataLoader for each split dataset
if use_custom_dataset:
  if dataset_is_split:
    # train_data = object_detector.DataLoader.from_pascal_voc(
    train_data = object_detector.DataLoader.from_pascal_voc(
        train_images_dir, train_annotations_dir, label_map=label_map)
    validation_data = object_detector.DataLoader.from_pascal_voc(
        val_images_dir, val_annotations_dir, label_map=label_map)
    test_data = object_detector.DataLoader.from_pascal_voc(
        test_images_dir, test_annotations_dir, label_map=label_map)
    print(f'train count: {len(train_data)}')
    print(f'validation count: {len(validation_data)}')
    print(f'test count: {len(test_data)}')
  else:
    train_dir, val_dir, test_dir = split_dataset(images_in, annotations_in,
                                                 val_split=0.1, test_split=0.1,
                                                 out_path='split-dataset')
    train_data = object_detector.DataLoader.from_pascal_voc(
        os.path.join(train_dir, 'images'),
        os.path.join(train_dir, 'annotations'), label_map=label_map)
    validation_data = object_detector.DataLoader.from_pascal_voc(
        os.path.join(val_dir, 'images'),
        os.path.join(val_dir, 'annotations'), label_map=label_map)
    test_data = object_detector.DataLoader.from_pascal_voc(
        os.path.join(test_dir, 'images'),
        os.path.join(test_dir, 'annotations'), label_map=label_map)

  print(f'train count: {len(train_data)}')
  print(f'validation count: {len(validation_data)}')
  print(f'test count: {len(test_data)}')


spec = model_spec.get('efficientdet_lite2')

model = object_detector.create(train_data=train_data,
                               model_spec=spec,
                               validation_data=validation_data,
                               epochs=10,
                               batch_size=5,
                               train_whole_model=True)


TFLITE_FILENAME = 'my_tflite_model.tflite'
LABELS_FILENAME = 'labels.txt'

model.export(export_dir='.', tflite_filename=TFLITE_FILENAME, label_filename=LABELS_FILENAME,
             export_format=[ExportFormat.TFLITE, ExportFormat.LABEL])


model.evaluate(test_data)

model.evaluate_tflite(TFLITE_FILENAME, test_data)