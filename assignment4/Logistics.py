
class Logistics(object):
    total_received = 0
    total_sent = 0

    def __init__(self, c):
        self._c = c

    def insert(self, logistic):
        self._c.execute("""
        INSERT INTO logistics(id, name, count_sent, count_received) VALUES(?, ?, ?, ?)
        """, [logistic.id, logistic.name, logistic.count_sent, logistic.count_received])
        Logistics.total_received += logistic.count_received
        Logistics.total_sent += logistic.count_sent

    def update_received(self, logistic_id, amount):
        cursor = self._c.cursor()
        cursor.execute("SELECT count_received FROM logistics WHERE id = ?", [logistic_id])
        curr_count_received = int(cursor.fetchone()[0])
        self._c.execute("UPDATE logistics SET count_received = ? WHERE id = ?", [curr_count_received + amount, logistic_id])
        Logistics.total_received += amount

    def update_sent(self, logistic_id, amount):
        cursor = self._c.cursor()
        cursor.execute("SELECT count_sent FROM logistics WHERE id = ?", [logistic_id])
        curr_count_sent = int(cursor.fetchone()[0])
        cursor.execute("UPDATE logistics SET count_sent = ? WHERE id = ?", [curr_count_sent + amount, logistic_id])
        Logistics.total_sent += amount
