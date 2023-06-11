import sys

TJ_list = []
TS_list = []

TJ_avg = 0
TS_avg = 0

with open(sys.argv[1], 'r') as my_file:
    line = my_file.readline()

    for line in my_file:
        if "," in line:
            TJ = line.split(",")[0]
            TS = line.split(",")[1].strip()

            TJ_list.append(int(TJ))
            TS_list.append(int(TS))


            #print("TJ: "+TJ)
            #print("TS: "+TS)
            

if(len(sys.argv) > 2):
    with open(sys.argv[2], 'r') as my_second_file:
        line = my_second_file.readline()

        for line in my_second_file:
            if "," in line:
                TJ = line.split(",")[0]
                TS = line.split(",")[1].strip()

                TJ_list.append(int(TJ))
                TS_list.append(int(TS))


for j in TJ_list:
    TJ_avg += j

TJ_avg = TJ_avg/len(TJ_list)
TJ_avg = TJ_avg/(pow(10,5))
print("TJ avg: "+str(TJ_avg)+" ms")

for s in TS_list:
    TS_avg += s

TS_avg = TS_avg/len(TS_list)
TS_avg = TS_avg/(pow(10,5))
print("TS avg: "+str(TS_avg)+" ms")