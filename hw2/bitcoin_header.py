import uuid
import time

class BitcoinHeader:
    def __init__(self, target, prev_hash=None):
        self.prev_hash = prev_hash if prev_hash is not None else self.generate_rand_num(length=16)
        self.current_hash = self.generate_rand_num(length=16)
        self.timestamp = time.time()
        self.nonce = self.generate_rand_num()
        self.target = target

    def generate_rand_num(self, length=8):
        return int(uuid.uuid4().hex[:length], 16)

    def get_values(self):
        return self.nonce, self.prev_hash, self.current_hash
