with open('random-bytes', 'r') as f:
    with open('random-longs', 'w') as out:
        line = f.readline()
        while (line != ""):
            numbers = line.split()
            mult = 1
            tot = 0
            for i in range(0, 8):
                num = int(numbers[i])
                if (i < 7):
                    tot += num * mult
                    mult *= 256
                else:
                    tot += (num & 0x7F) * mult
                    tot -= (num & 0x80) * mult
            out.write(str(tot) + '\n')
            line = f.readline()
