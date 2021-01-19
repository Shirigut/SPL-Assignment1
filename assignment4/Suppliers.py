
class Suppliers(object):
    def __init__(self, c):
        self._c = c

    def insert(self, supplier):
        self._c.execute("""
        INSERT INTO suppliers(id, name, logistic) VALUES(?, ?, ?)
        """, [supplier.id, supplier.name, supplier.logistic])

    def get_logistic_id(self, name):
        cursor = self._c.cursor()
        cursor.execute("SELECT logistic FROM suppliers WHERE name = ?", [name])
        return cursor.fetchone()[0]

    def get_supplier_id(self, name):
        cursor = self._c.cursor()
        cursor.execute("SELECT id FROM suppliers WHERE name = ?", [name])
        return cursor.fetchone()[0]

