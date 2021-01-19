
class Clinics(object):
    demand = 0

    def __init__(self, c):
        self._c = c

    def insert(self, clinic):
        self._c.execute("""
        INSERT INTO clinics(id, location, demand, logistic) VALUES(?, ?, ?, ?)
        """, [clinic.id, clinic.location, clinic.demand, clinic.logistic])
        Clinics.demand += clinic.demand

    def update_demand(self, location, amount):
        cursor = self._c.cursor()
        cursor.execute("SELECT demand FROM clinics WHERE location = ?", [location])
        curr_demand = cursor.fetchone()[0]
        self._c.execute("UPDATE clinics SET demand = ? WHERE location = ?", [curr_demand - amount, location])
        if Clinics.demand < amount:
            Clinics.demand = 0
        else:
            Clinics.demand -= amount

    def get_logistic (self, location):
        cursor = self._c.cursor()
        cursor.execute("SELECT logistic FROM clinics WHERE location = ?", [location])
        return cursor.fetchone()[0]
