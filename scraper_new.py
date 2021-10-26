from bs4 import BeautifulSoup
from selenium import webdriver
from selenium.webdriver.firefox.options import Options
import re
import numpy as np
import html

print("Loading...")
timeout = 10
puzzle = np.array([''] * 25)
clues = []
clue_numbers = []
puzzle_url = "https://www.nytimes.com/crosswords/game/mini" #"https://www.nytimes.com/crosswords/game/special/tricky-clues-mini" #

options = Options()
options.headless = False
driver = webdriver.Firefox(options=options)

driver.get(puzzle_url)
driver.implicitly_wait(timeout)

driver.find_element_by_xpath("//*[text()='OK']").click()
driver.find_element_by_xpath("//*[text()='reveal']").click()
driver.find_elements_by_xpath("//*[text()='Puzzle']")[1].click()
driver.find_element_by_xpath("//*[text()='Reveal']").click()
driver.find_element_by_class_name("ModalBody-closeX--2Fmp7").click()

page_source = driver.page_source
driver.close()

soup = BeautifulSoup(page_source, 'html.parser')
for element in soup.findAll(class_="Cell-block--1oNaD"):
    puzzle[int(element.get("id")[8:])] = '#'

for element in soup.findAll(class_="Clue-text--3lZl7"):
    clues.append(html.unescape(re.search("<span class=\"Clue-text--3lZl7\">(.*?)</span>", str(element)).group()[31:-7]))

for element in soup.findAll(class_="Clue-label--2IdMY"):
    clue_numbers.append(int(re.search(">(.*?)<", str(element)).group()[1:-1]))

counter = 0
digit_places = {}

#print(puzzle, clues, clue_numbers)
#print(re.findall("(>[A-Z1-9]<)", str(soup.findAll("text"))))

letters = re.findall("(>[A-Z1-9]<)", str(soup.findAll("text")))

store = 0
old_letter = ''
for e in range(len(letters)-1, -1, -1):
    if letters[e][1].isalpha():
        if letters[e][1] == old_letter:
            store += 1
        else:
            store = 0
            old_letter = letters[e][1]
        if store == 2:
            del letters[e]
            del letters[e+1]
            old_letter = ''
            store = 0
    else:
        old_letter = ''
        store = 0


for e in letters:
    print(e)
    while puzzle[counter] == '#':
        counter += 1
    if e[1].isdigit():
        digit_places[int(e[1])] = (counter // 5, counter % 5)
    else:
        puzzle[counter] = html.unescape(e[1])
        counter += 1
# put things to words
puzzle = puzzle.reshape((5, 5))
wordlist = []

rotated_puzzle = np.rot90(puzzle)

for e in clue_numbers[:5]:
    wordlist.append(''.join(puzzle[digit_places[e][0]]))

for e in clue_numbers[5:]:
    wordlist.append(''.join(rotated_puzzle[4-digit_places[e][1]]))


str_digit_places = str(digit_places).replace(" ", "").replace("),", "); ")

print(wordlist)
print(clues)
print(clue_numbers)
print(str_digit_places)
print(puzzle)

clues = [clue.encode(encoding = 'ascii', errors = 'xmlcharrefreplace').decode() for clue in clues]
np.savetxt("aiFood.txt", [clue_numbers, wordlist, clues, str_digit_places, puzzle.reshape(25)], delimiter=",", fmt="%s")


