# coding: utf-8

from py4j.java_gateway import JavaGateway

if __name__ == '__main__':
    gateway = JavaGateway()
    addition_app = gateway.entry_point
    print addition_app.testAdd(2,9)
    print addition_app.addition(2,10)
