from cv2 import imwrite, VideoCapture
import time
import argparse
import os
import json


LEGAL_LOCS = ['hall', 'room', 'open', 'stair']
LEGAL_OBJS = ['mid', 'left', 'right']


class ImageCaptureDevice:
    def __init__(self, image_dest, data_dest, loc, object):
        self.image_dest = os.path.abspath(image_dest)
        self.data_dest = os.path.abspath(data_dest)
        self.curr_loc = loc
        self.object = object
        if self.__init_capture_device() == -1:
            exit(-1)

    def capture_image_data(self, id):
        msg, img = self.cap_device.read()
        # Check if the image was read succesfully
        if msg:
            # the identifier of every image is the precise time and
            # date when it is taken
            file_name = time.strftime("%Y%m%d_%H%M%S")

            # Write the image to the desired location
            imwrite(os.path.join(self.image_dest, file_name + '.jpg'), img)

            # Write the data to the desired location
            data = {
                'id': file_name,
                'location': self.curr_loc,
                'object': self.object,
                'init_im_dest': self.image_dest,
                'num_in_burst': id
            }
            with open(os.path.join(self.data_dest, file_name + '.json'), 'w') as outfile:
                json.dump(data, outfile, indent=4)

    def __init_capture_device(self):
        try:
            self.cap_device = VideoCapture(2)
        except Exception:
            print('Failed to open camera, check camera index')
            return -1


def main():
    parser = argparse.ArgumentParser(
        description='Create a series of pictures with the given attributes \
                    (location, object placement)')
    parser.add_argument('location', help='location of the series of picture \
                        (hall, room, open, stair)')
    parser.add_argument('image_dest', help='path to the desired output \
                        directory for images')
    parser.add_argument('--data_dest', help='path to the desired output \
                        directory for data', default=None)
    parser.add_argument('--object', help='placement of an object in front \
                        of the camera (mid, left, right)', default=None)
    parser.add_argument('--burst', type=int, help='number of pictures in the burst',
                        default=10)
    parser.add_argument('--interval', type=int, help='Time in between each capture',
                        default=10)
    args = parser.parse_args()

    # Check if input parameters for burst are correct
    if args.location not in LEGAL_LOCS:
        print('Location must be one of the following: hall, room, open, stair')
        exit(-1)
    if args.object not in LEGAL_OBJS and args.object is not None:
        print('Object must be one of the following: mid, left, right')
        exit(-1)

    # Initialize data capture object for current burst
    args.data_dest = args.image_dest if not args.data_dest else None
    print('Initializing Image Capture Device with following parameters: \n\
                    - image_dest: {}\n\
                    - data_dest: {}\n\
                    - location: {}\n\
                    - object: {}'.format(args.image_dest,
                        args.data_dest, args.location, args.object))
    im_cap = ImageCaptureDevice(
        args.image_dest, args.data_dest, args.location, args.object)

    for i in range(args.burst):
        print('Capturing and saving Image #{}'.format(i))
        im_cap.capture_image_data(i)
        print('Waiting {}s...'.format(args.interval))
        time.sleep(args.interval)


if __name__ == '__main__':
    main()
