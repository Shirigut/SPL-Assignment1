
class Vaccines(object):
    inventory = 0

    def __init__(self, c):
        self._c = c

    def insert(self, vaccine):
        self._c.execute("""
        INSERT INTO vaccines(id, date, supplier, quantity) VALUES(?, ?, ?, ?)
        """, [vaccine.id, vaccine.date, vaccine.supplier, vaccine.quantity])
        Vaccines.inventory += vaccine.quantity

    def get_last_id(self):
        cursor = self._c.cursor()
        cursor.execute("SELECT MAX (id) FROM vaccines")
        max_id = int(cursor.fetchone()[0])
        return max_id+1

    def update_vaccines(self, amount):
        original_amount = amount
        cursor = self._c.cursor()
        cursor.execute("SELECT date FROM vaccines")
        dates = cursor.fetchall()

        list(dates)
        i = 0
        while amount > 0 and i < len(dates):
            cursor.execute("SELECT MIN (date) FROM vaccines")
            all = cursor.fetchall()
            date = (all[0])[0]
            cursor.execute("SELECT quantity, id FROM vaccines WHERE date = ?", [date])
            tuple = cursor.fetchone()
            quantity = tuple[0]
            curr_id = tuple[1]
            if amount >= quantity:
                self._c.execute("DELETE FROM vaccines WHERE id = ?", [curr_id])
            else:
                self._c.execute("UPDATE vaccines SET quantity = ? WHERE id = ?", [quantity - amount, curr_id])
            amount = amount - quantity
            i = i + 1

        if Vaccines.inventory < original_amount:
            Vaccines.inventory = 0
        else:
            Vaccines.inventory -= original_amount