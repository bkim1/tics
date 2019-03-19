import socket
import sys
import json
import hashlib
from threading import Thread, current_thread


MAX_BUFFER_SIZE=1024


def start_server(host, port):
    """ Creates a new server socket that waits for a new connection.
        Upon a new connection, it will spawn a new thread to handle 
        verifying the data sent over. """

    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    soc.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
    print('Socket created')

    try:
        soc.bind((host, port))
    except:
        print(f'Bind Failed... Error: {str(sys.exc_info())}')
        sys.exit()
    else:
        soc.listen(5)
        print('Socket is now listening')

        while True:
            try:
                connection, addr = soc.accept()
            except KeyboardInterrupt:
                break
            else:
                Thread(target=verify_message, args=(connection,)).start()
    finally:
        print('\nClosing socket...')
        soc.close()


def verify_message(connection):
    """ Takes a new connection and processes the message sending back
        whether or not the message contains a valid hashed value """

    with connection:
        try:
            message = receive_message(connection)
        except OverflowError as err:
            print(str(err))
        else:
            # Recalculate the hash value to verify it adheres to the difficulty
            byte_val = str(message['nonce'] + \
                           message['prev_hash'] + \
                           message['block_hash']).encode('utf-8')
            temp_hash = int(hashlib.sha256(byte_val).hexdigest(), 16)

            if temp_hash < 2**message['difficulty']:
                send_val = { 'valid': True }
            else:
                send_val = { 'valid': False }

            # Send back the message to the solver 
            print(f'{current_thread().name}: Sending response back!')
            connection.sendall(json.dumps(send_val).encode('utf-8'))

    print(f'{current_thread().name}: Closed down the connection and thread!\n')


def receive_message(connection):
    """ Receives data sent from client and returns a JSON object """

    client_input = connection.recv(MAX_BUFFER_SIZE)
    input_size = sys.getsizeof(client_input)

    if input_size > MAX_BUFFER_SIZE:
        raise OverflowError(f'Input > Max buffer size: {input_size}')

    print(f'{current_thread().name}: Received message from solver!')
    return json.loads(client_input)


if __name__ == '__main__':
    if len(sys.argv) == 3:
        host, port = sys.argv[1], sys.argv[2]
    else:
        host, port = '127.0.0.1', 8888

    start_server(host, port)
