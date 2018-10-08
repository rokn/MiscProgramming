from bs4 import BeautifulSoup
import requests
import threading
from notify_run import Notify
notify = Notify()

def sendmessage(message):
    subprocess.Popen(['notify-send', message])
    return

data = {'txtUserName': 'amindov', 'txtPassword': '7410852963', 'btnSubmit': 'Влез', '__VIEWSTATE': '', '__EVENTTARGET': '', '__EVENTARGUMENT': ''}

# A Session object will persist the login cookies.
def check():
    threading.Timer(60.0, check).start() # called every minute
    with requests.Session() as s:
        page = s.get('https://susi.uni-sofia.bg/ISSU/forms/Login.aspx')
        soup = BeautifulSoup(page.content, 'lxml')
        data["__VSTATE"] = soup.select_one("#__VSTATE")["value"]
        data["__EVENTVALIDATION"] = soup.select_one("#__EVENTVALIDATION")["value"]
        s.post('https://susi.uni-sofia.bg/ISSU/forms/Login.aspx', data=data)
        page = s.get('https://susi.uni-sofia.bg/ISSU/forms/students/ElectiveDisciplinesSubscribe.aspx')
        soup = BeautifulSoup(page.content, "lxml")
        message = soup.select_one('#PageError1_lblMessage')
        print(message)
        print(message.text)
        if not message or message.text != 'В момента няма стартирана кампания за избираеми дисциплини.':
            print("WARNING!!!!")
            notify.send("WARNING!!!!")


check()
