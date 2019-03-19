import socket
import sys
import json
import time
import hashlib

from bitcoin_header import BitcoinHeader


MAX_BUFFER_SIZE=1024


def solve_puzzle():
    """ Generates new BitcoinHeader, solves the puzzle, and returns
        the updated BitcoinHeader to be sent to the verifier """ 
    
    header = BitcoinHeader()
    byte_val = str(header.nonce + \
                   header.prev_hash + \
                   header.block_hash).encode('utf-8')
    temp_hash = int(hashlib.sha256(byte_val).hexdigest(), 16)

    # Continually increment the nonce and re-hash the value until
    # the value is below the difficulty
    while temp_hash > 2**header.difficulty:
        header.nonce += 1
        byte_val = str(header.nonce + \
                       header.prev_hash + \
                       header.block_hash).encode('utf-8')
        temp_hash = int(hashlib.sha256(byte_val).hexdigest(), 16)

    # Update timestamp and return the header
    header.timestamp = time.time()
    return header


def main():
    # Get the host IP and port if specified
    if len(sys.argv) == 3:
        host, port = sys.argv[1], sys.argv[2]
    else:
        host, port = '127.0.0.1', 8888

    print('Starting to solve puzzle...')
    header = solve_puzzle()
    print('Solved the puzzle! Opening socket now!')

    # Create and connect the socket to the verifier
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
        try:
            s.connect((host, port))
        except:
            print('Error connecting to verifier')
            sys.exit()

        # Send solved puzzle to verifier as JSON object
        # Similar to Bitcoin's implementation in their blockchain.cpp
        # file under src/rpc/
        s.sendall(header.get_json_str().encode('utf-8'))

        # Wait for verifier to send message back and unpack it as a JSON object
        rcv_data = json.loads(s.recv(MAX_BUFFER_SIZE))
        print(f'Received data from verifier: {rcv_data}')

    print('Closed socket and exiting program!')


if __name__ == '__main__':
    main()
