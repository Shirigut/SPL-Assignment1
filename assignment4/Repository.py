import atexit
import sqlite3
from Vaccines import Vaccines
from Clinics import Clinics
from Suppliers import Suppliers
from Logistics import Logistics
from Vaccine import Vaccine


class _Repository(object):

    def __init__(self):
        self._c = sqlite3.connect('database.db')
        self.clinics = Clinics(self._c)
        self.suppliers = Suppliers(self._c)
        self.vaccines = Vaccines(self._c)
        self.logistics = Logistics(self._c)

    def close(self):
        self._c.commit()
        self._c.close()

    def create_tables(self):
        self._c.executescript("""
            CREATE TABLE clinics (
                id INT   PRIMARY KEY,
                location TEXT   NOT NULL,
                demand   INT    NOT NULL,
                logistic INT    REFERENCES logistics(id) );
                
            CREATE TABLE vaccines (
                id       INT    PRIMARY KEY,
                date     DATE   NOT NULL,
                supplier INT    NOT NULL,
                quantity INT    NOT NULL,
                 
                FOREIGN KEY(supplier) REFERENCES suppliers(id)
                );
            
            CREATE TABLE suppliers (
                id       INTEGER    PRIMARY KEY,
                name     TEXT       NOT NULL,
                logistic INTEGER    NOT NULL,
                 
                FOREIGN KEY(logistic) REFERENCES logistics(id)
                );
                
            CREATE TABLE logistics (
                id              INT     PRIMARY KEY,
                name            TEXT    NOT NULL,
                count_sent      INT     NOT NULL,
                count_received  INT     NOT NULL );
            """)

    def receive_shipment(self, name, amount, date):
        supplier_id = Suppliers.get_supplier_id(self.suppliers, name)
        vaccine_id = Vaccines.get_last_id(self.vaccines)
        vac = Vaccine(vaccine_id, date, supplier_id, amount)
        Vaccines.insert(self.vaccines, vac)
        logistic_id = self.suppliers.get_logistic_id(name)
        self.logistics.update_received(logistic_id, amount)
        repo._c.commit()

    def send_shipment(self, location, amount):
        Clinics.update_demand(self.clinics, location, amount)
        Vaccines.update_vaccines(self.vaccines, amount)
        logistic_id = Clinics.get_logistic(self.clinics, location)
        Logistics.update_sent(self.logistics, logistic_id, amount)
        repo._c.commit()

    def insert_to_table(self, table_type, dto):
        if table_type == "vaccines":
            Vaccines.insert(self.vaccines, dto)
        elif table_type == "suppliers":
            Suppliers.insert(self.suppliers, dto)
        elif table_type == "clinics":
            Clinics.insert(self.clinics, dto)
        else:
            Logistics.insert(self.logistics, dto)
        self._c.commit()

    def get_state(self):
        curr_output = (Vaccines.inventory, Clinics.demand, Logistics.total_received, Logistics.total_sent)
        curr_output = str(curr_output[0]) + ',' + str(curr_output[1]) + ',' + str(curr_output[2]) + ',' + str(curr_output[3])
        return curr_output

repo = _Repository()
atexit.register(repo.close)
