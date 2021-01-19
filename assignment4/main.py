from Repository import repo
import sys
from Vaccine import Vaccine
from Supplier import Supplier
from Clinic import Clinic
from Logistic import Logistic

if __name__ == '__main__':
    repo.create_tables()
    with open(sys.argv[1]) as inputFile:
        input = inputFile.readlines()
        i = 0
        set_tables_by_num = []
        for line in input:
            if i < len(input) - 1:
                line = line[:-1]
            if i == 0:
                set_tables_by_num = line.split(',')
                for j in range (len(set_tables_by_num)):
                    set_tables_by_num[j] = int(set_tables_by_num[j])
            else:
                line = line.replace("ג", '-')
                line = line.replace('ˆ', '')
                line = line.replace('’', '')
                args = line.split(",")

                if i <= set_tables_by_num[0]:
                    curr_vaccine = Vaccine(int(args[0]), args[1], int(args[2]), int(args[3]))
                    repo.insert_to_table('vaccines', curr_vaccine)
                elif i <= set_tables_by_num[0] + set_tables_by_num[1]:
                    curr_supplier = Supplier(int(args[0]), args[1], int(args[2]))
                    repo.insert_to_table('suppliers', curr_supplier)
                elif i <= set_tables_by_num[0] + set_tables_by_num[1] + set_tables_by_num[2]:
                    curr_clinic = Clinic(int(args[0]), args[1], int(args[2]), int(args[3]))
                    repo.insert_to_table('clinics', curr_clinic)
                else:
                    curr_logistic = Logistic(int(args[0]), args[1], int(args[2]), int(args[3]))
                    repo.insert_to_table('logistic', curr_logistic)
            i = i + 1
    inputFile.close()

    output = ""
    with open(sys.argv[2]) as ordersFile:
        orders = ordersFile.readlines()
    order = []
    i = 0
    for line in orders:
        order = line.split(",")
        if i < len(orders) - 1:
            x = order[len(order) - 1]
            x = x[:-1]
            order[len(order) - 1] = x

        if len(order) == 3:
            order[2] = order[2].replace("ג", '-')
            order[2] = order[2].replace('ˆ', '')
            order[2] = order[2].replace('’', '')
            repo.receive_shipment(order[0], int(order[1]), order[2])
        else:
            repo.send_shipment(order[0], int(order[1]))
        output = output + repo.get_state() + '\n'
        i += 1
    ordersFile.close()

    output_file = open(sys.argv[3], "w")
    output_file.write(output)
    output_file.close()




















