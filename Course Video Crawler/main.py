
from bs4 import BeautifulSoup
import requests
import re
import os

def crawl(url, filename):

    if not url.endswith("mp4"):
        return

    if not filename.endswith("mp4"):
        return

    # download image using GET
    rawVideo = requests.get(url, stream=True)
    # save the image received into the file
    with open("./data/" + filename, 'wb') as fd:
        for chunk in rawVideo.iter_content(chunk_size=1024):
            fd.write(chunk)

if __name__ == "__main__":
    print("Creating 'data' folder in the current directory ...")
    os.mkdir("data")
    url = "http://cs.gettysburg.edu/~tneller/cs371/away/covid19.html"
    src = requests.get(url).text
    soup = BeautifulSoup(src, 'html.parser')
    link_list = soup.find_all('a')

    for link in link_list:
        video_link = link.get('href')
        if video_link.endswith("mp4"):
            p = re.compile("\/[A-Za-z0-9-]+.mp4")
            name = re.findall(p, video_link)[0][1:]
            print(f"Crawling video {name} ...")
            crawl(video_link, name)

    print("Done!")