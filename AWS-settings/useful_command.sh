# global command
# --- viewing
yum list
yum list installed
chkconfig --list <service> # list all known services
service --status-all | less # running services
service httpd status # apache service status
sudo ntsysv # Turn on/off service
# --- removing
yum erase <pkg(optional wildcard)>

# command explain
# --- start <name> automatically when OS start
chkconfig <name> on
# --- control services
service <name> <start|stop|restart|reload>

# start apache http server
chkconfig httpd on
/etc/init.d/httpd start

# initial postgresql
service postgresql initdb
chkconfig postgresql on

# psql command
CREATE USER user WITH PASSWORD 'myPass'
CERATE DATABASE dbname
GRANT ALL PRIVILEGES ON DATABASE dbname TO user
ALTER USER user WITH PASSWORD 'newPass'
SHOW config_file
