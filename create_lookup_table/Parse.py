from HTMLParser import HTMLParser
import sqlite3
import csv
import os

# create a subclass and override the handler methods
class MyHTMLParser(HTMLParser):
    all_data = '\n'
    
    def handle_data(self, data):
        self.all_data= self.all_data + data

    def get_data(self):
        return self.all_data

def main():
    data = ''
    with open('raw.txt','r') as file:
        data = data + file.read()

    htmlStrip = MyHTMLParser()
    htmlStrip.feed(data)
    with open('parsed.txt','w') as file:
        file.write(htmlStrip.get_data())

def convert_csv():
    os.remove('color.db')
    conn = sqlite3.connect('color.db')
    c = conn.cursor()
    c.execute('create table colors (_id integer primary key, name text, hex real, red real, green real, blue real)')

    with open('name_hex_rgb.csv','rb') as csvfile:
        reader = csv.reader(csvfile,delimiter=',',quotechar='|')
        for row in reader:
            print row[0],row[1]
            c.execute("insert into colors (name,hex,red,green,blue) values (?,?,?,?,?)",(row[0],row[1],row[2],row[3],row[4]))

        conn.commit()
        conn.close()
    
convert_csv()
