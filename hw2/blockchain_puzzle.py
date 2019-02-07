import time
import os
import uuid

import matplotlib


class BitcoinHeader():

    def __init__(self):
        self.hash_val = None
        self.timestamp = time.now()
        self.prev = None
        self.root = None
        self.nonce = self.generate_nonce()
        self.target = None

    def generate_nonce(self, length=8):
        return uuid.uuid4().hex[:length]

    def set_new_nonce(self):
        self.nonce = self.generate_nonce()


def create_boxplot(vector):
    pass


