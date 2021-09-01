# import the necessary libraries
from   sklearn.datasets import load_boston
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
from   statsmodels.stats.outliers_influence import variance_inflation_factor as vif
from   sklearn.decomposition import PCA
from   sklearn.preprocessing import RobustScaler
from   sklearn.preprocessing import MinMaxScaler
from   sklearn.model_selection import train_test_split
import tensorflow as tf
from   tensorflow.keras import Sequential
from   tensorflow.keras.layers import Dense


