from bs4 import BeautifulSoup
from urllib.request import urlopen
import nltk
nltk.download('punkt')
from nltk.tokenize import sent_tokenize
from PIL import Image
import requests

import time
PRINT_MODE = False


#################################################
# Code for crawling allrecipes.com for recipes.
#################################################


class Recipe(object):

    def __init__(self, url, title, ingredients, directions, info_dict, image):
        self.recipe_dict = {}
        self.recipe_dict["RecipeId"] = url.split("/")[-3]
        self.recipe_dict["url"] = url
        self.recipe_dict["Title"] = title
        self.recipe_dict["Ingredients"] = ingredients
        self.recipe_dict["Directions"] = directions
        self.recipe_dict["InfoDict"] = info_dict
        self.recipe_dict["Image"] = image


#################################################


def get_title(recipe_url="https://www.allrecipes.com/recipe/8497586/chocolate-strawberry-cheesecake/"):
    """
    :param soup: soup object for a specific recipe page
    :return: the recipe's title
    """
    if PRINT_MODE:
        print("recipe url:", recipe_url)

    source_data = urlopen(recipe_url).read()
    soup = BeautifulSoup(source_data, 'html.parser')
    title = soup.find("h1", {"class": "comp type--lion article-heading mntl-text-block"})
    return title.text.strip()


def get_ingredient_lines(recipe_url):
    """
    :param soup: soup object for a specific recipe page
    :return: the recipe's ingredients as an array of strings
    """

    if PRINT_MODE:
        print("recipe url:", recipe_url)

    source_data = urlopen(recipe_url).read()
    soup = BeautifulSoup(source_data, 'html.parser')
    ingredient_blocks = soup.find_all("li", {"class": 'mntl-structured-ingredients__list-item'})
    text_getter = lambda x: (x.text).strip().replace("\u2009", "")
    ingredients = list(map(text_getter, ingredient_blocks))

    return ingredients


def get_directions(recipe_url):
    """
    :param soup: soup object for a specific recipe page
    :return: the recipe's directions as an array of strings
    """
    if PRINT_MODE:
        print("recipe url:", recipe_url)

    source_data = urlopen(recipe_url).read()
    soup = BeautifulSoup(source_data, 'html.parser')

    # directions_blocks = soup.find_all("li",
    #                                   {"class": "comp mntl-sc-block-group--LI mntl-sc-block mntl-sc-block-startgroup"})
    directions_blocks = soup.find_all("li",
                                      {"class": "comp mntl-sc-block-group--LI mntl-sc-block mntl-sc-block-startgroup"})
    directions = []
    for block in directions_blocks:
        directions_block = block.findAll("p",
                                         {"class": "comp mntl-sc-block mntl-sc-block-html"})
        text_getter = lambda x: (x.text).strip()
        directions.extend(list(map(text_getter, directions_block)))

    directions_spltd = []
    for d in directions:
        splited = sent_tokenize(d)
        directions_spltd += splited

    i = 0
    results = []
    while i < len(directions_spltd):
        splited = directions_spltd[i]
        if i + 1 < len(directions_spltd) and directions_spltd[i + 1][0] == '(':
            splited += " "
            splited += directions_spltd[i + 1]
            i += 1
        results += [splited]
        i += 1
    i = 0
    result = []
    while i < len(results):
        splited = results[i]
        while i + 1 < len(results) and splited.count("(") > splited.count(")"):
            splited += " "
            splited += results[i + 1]
            i += 1
        result += [splited]
        i += 1
    directions_spltd = result
    directions_spltd = [item.strip() for item in directions_spltd]
    directions_spltd = [item for item in directions_spltd if item != '.']
    return directions_spltd


def get_time_and_serving_info(soup):
    """
    :param soup: soup object for a specific recipe page
    :return: a dictionary that holds the recipe's time and serving info (keys: prep, cook, total, servings)
    """
    info_dict = {}
    info_block = soup.find("div", {"class": "mntl-recipe-details__content"})
    all_labels = info_block.findAll("div", {"class": "mntl-recipe-details__label"})
    all_labels = [item.text.lower().strip().split(":")[0] for item in all_labels]
    all_values = info_block.findAll("div", {"class": "mntl-recipe-details__value"})
    all_values = [item.text.lower().strip() for item in all_values]
    for i in range(len(all_labels)):
        info_dict[all_labels[i]] = all_values[i]

    return info_dict


def get_image(recipe_url):
    if PRINT_MODE:
        print("recipe url:", recipe_url)

    source_data = urlopen(recipe_url).read()
    soup = BeautifulSoup(source_data, 'html.parser')
    image_block = soup.find("div", {"class": "primary-image__media"})
    block = image_block.find("div", {"class": "img-placeholder"})
    images = block.findAll('img')
    img = Image.open(requests.get(images[0]['src'], stream=True).raw)
    return img


def parse_recipe(recipe_url):
    """
    Parses a single recipe page
    :param recipe_url: the recipe's url
    :return: a recipe object
    """

    if PRINT_MODE:
        print("recipe url:", recipe_url)

    source_data = urlopen(recipe_url).read()
    soup = BeautifulSoup(source_data, 'html.parser')
    title = get_title(soup)
    ingredients = get_ingredient_lines(soup)  # array of strings
    directions = get_directions(soup)  # array of strings

    directions_spltd = []
    for d in directions:
        splited = sent_tokenize(d)
        directions_spltd += splited

    i = 0
    result = []
    while i < len(directions_spltd):
        splited = directions_spltd[i]
        if i + 1 < len(directions_spltd) and directions_spltd[i + 1][0] == '(':
            splited += " "
            splited += directions_spltd[i + 1]
            i += 1
        result += [splited]
        i += 1
    directions_spltd = result

    directions_spltd = [item.strip() for item in directions_spltd]
    directions_spltd = [item for item in directions_spltd if item != '.']

    info_dict = get_time_and_serving_info(soup)
    image = get_image(soup)

    if PRINT_MODE:
        print("title: " + title)
        print("ingredients: " + str(ingredients))
        print("directions: " + str(directions))
        print("directions (splitted): " + str(directions_spltd))
        print("info dict: " + str(info_dict))

    recipe = Recipe(recipe_url, title, ingredients, directions, info_dict, image)

    return recipe


if __name__ == '__main__':
    print(get_title("https://www.allrecipes.com/recipe/229669/glazed-carrots/"))
