import uuid
import time
import json

class BitcoinHeader:
    """ Class representing a Bitcoin Header object """

    def __init__(self, difficulty=235, prev_hash=None):
        self.prev_hash = prev_hash if prev_hash is not None \
            else self.generate_rand_num(length=16)
        self.block_hash = self.generate_rand_num(length=16)
        self.timestamp = time.time()
        self.nonce = self.generate_rand_num()
        self.difficulty = difficulty

    def generate_rand_num(self, length=8):
        return int(uuid.uuid4().hex[:length], 16)

    def get_values(self):
        return self.nonce, self.prev_hash, self.block_hash

    def get_json_str(self):
        return json.dumps({
            'prev_hash': self.prev_hash,
            'block_hash': self.block_hash,
            'timestamp': self.timestamp,
            'nonce': self.nonce,
            'difficulty': self.difficulty
        })
