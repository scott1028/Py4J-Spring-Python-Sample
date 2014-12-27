# coding: utf-8

from py4j.java_gateway import JavaGateway
from py4j.java_gateway import GatewayClient

if __name__ == '__main__':
	# gateway = JavaGateway()
	# you can use remote server
    gateway = JavaGateway(
    	gateway_client=GatewayClient(
    		address='127.0.0.1',
    		port=25333,
    		auto_close=True,
    		gateway_property=None))

    addition_app = gateway.entry_point
    print addition_app.testAdd(2,9)
    print addition_app.addition(2,10)
