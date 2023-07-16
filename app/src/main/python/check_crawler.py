from bs4 import BeautifulSoup
from urllib.request import urlopen
import nltk

nltk.download('punkt')
from nltk.tokenize import sent_tokenize
from PIL import Image
import requests

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


LINKS = ["https://www.allrecipes.com/recipe/8458926/air-fryer-cherry-cream-cheese-croissants/",
         "https://www.allrecipes.com/recipe/8462068/sheet-pan-buttermilk-pancakes/",
         "https://www.allrecipes.com/recipe/8505902/orange-cinnamon-rolls/",
         "https://www.allrecipes.com/recipe/8501678/tadpole-in-the-hole-breakfast-sausage-and-kale-dutch-baby/",
         "https://www.allrecipes.com/recipe/20513/classic-waffles/",
         "https://www.allrecipes.com/recipe/21649/sausage-balls/",
         "https://www.allrecipes.com/recipe/71803/quick-and-easy-home-fries/",
         "https://www.allrecipes.com/recipe/16895/fluffy-french-toast/",
         "https://www.allrecipes.com/recipe/220961/leftover-ham-n-potato-casserole/",
         "https://www.allrecipes.com/recipe/20876/crustless-spinach-quiche/",

         "https://www.allrecipes.com/recipe/14385/pasta-salad/",
         "https://www.allrecipes.com/recipe/47717/reuben-sandwich-ii/",
         "https://www.allrecipes.com/recipe/130979/day-after-thanksgiving-turkey-carcass-soup/",
         "https://www.allrecipes.com/recipe/90500/beet-salad-with-goat-cheese/",
         "https://www.allrecipes.com/recipe/228126/caprese-salad-with-balsamic-reduction/",
         "https://www.allrecipes.com/recipe/234914/how-to-make-tomato-bisque/",
         "https://www.allrecipes.com/recipe/244231/hot-dog-mummies/",
         "https://www.allrecipes.com/recipe/141314/the-best-vegetable-salad/",
         "https://www.allrecipes.com/recipe/232719/chef-johns-pita-bread/",
         "https://www.allrecipes.com/recipe/240559/traditional-gyros/",

         "https://www.allrecipes.com/recipe/13107/miso-soup/",
         "https://www.allrecipes.com/recipe/18465/gnocchi-i/",
         "https://www.allrecipes.com/recipe/80969/simple-turkey-chili/",
         "https://www.allrecipes.com/recipe/229692/easy-sweet-potato-casserole/",
         "https://www.allrecipes.com/recipe/215189/fruit-and-yogurt-smoothie/",
         "https://www.allrecipes.com/recipe/241019/spinach-and-banana-power-smoothie/",
         "https://www.allrecipes.com/recipe/27252/best-potatoes-youll-ever-taste/",
         "https://www.allrecipes.com/recipe/14126/country-fried-squash/",
         "https://www.allrecipes.com/recipe/144346/roasted-garlic-lemon-broccoli/",
         "https://www.allrecipes.com/recipe/229892/amazing-apple-butter/",

         "https://www.allrecipes.com/recipe/8539179/tiktok-candied-kielbasa-bites/",
         "https://www.allrecipes.com/recipe/8457754/creamy-whipped-feta/",
         "https://www.allrecipes.com/recipe/8462063/air-fryer-halloumi-cheese/",
         "https://www.allrecipes.com/recipe/8521921/pizza-sliders/",
         "https://www.allrecipes.com/recipe/8498970/copycat-dots-pretzels/",
         "https://www.allrecipes.com/recipe/275252/grilled-halloumi-with-herbed-berry-salsa/",
         "https://www.allrecipes.com/recipe/26819/hot-artichoke-and-spinach-dip-ii/",
         "https://www.allrecipes.com/recipe/216756/baked-ham-and-cheese-party-sandwiches/",
         "https://www.allrecipes.com/recipe/189930/southern-pimento-cheese/",
         "https://www.allrecipes.com/recipe/230225/bisquick-sausage-balls/",

         "https://www.allrecipes.com/recipe/129820/apple-spinach-salad/",
         "https://www.allrecipes.com/recipe/8524004/cold-green-bean-salad-with-lemon-vinaigrette/",
         "https://www.allrecipes.com/recipe/18085/hot-german-potato-salad-iii/",
         "https://www.allrecipes.com/recipe/147103/delicious-egg-salad-for-sandwiches/",
         "https://www.allrecipes.com/recipe/237984/marinated-cucumber-onion-and-tomato-salad/",
         "https://www.allrecipes.com/recipe/90500/beet-salad-with-goat-cheese/",
         "https://www.allrecipes.com/recipe/14276/strawberry-spinach-salad-i/",
         "https://www.allrecipes.com/recipe/141314/the-best-vegetable-salad/",
         "https://www.allrecipes.com/recipe/16409/spinach-and-strawberry-salad/",
         "https://www.allrecipes.com/recipe/149799/easy-cold-pasta-salad/",

         "https://www.allrecipes.com/recipe/8530802/orange-marmalade/",
         "https://www.allrecipes.com/recipe/8460058/animal-style-fries/",
         "https://www.allrecipes.com/recipe/8469209/moist-and-savory-stuffing/",
         "https://www.allrecipes.com/recipe/8516795/tiktok-crispy-bubble-potatoes/",
         "https://www.allrecipes.com/recipe/8486301/sour-cream-mashed-potatoes/",
         "https://www.allrecipes.com/recipe/183835/quick-tartar-sauce/",
         "https://www.allrecipes.com/recipe/26482/candied-yams/",
         "https://www.allrecipes.com/recipe/36925/garlic-butter/",
         "https://www.allrecipes.com/recipe/53304/cream-corn-like-no-other/",
         "https://www.allrecipes.com/recipe/82659/old-fashioned-onion-rings/",

         "https://www.allrecipes.com/recipe/8499644/chicken-minestrone-soup/",
         "https://www.allrecipes.com/recipe/16678/slow-cooker-taco-soup/",
         "https://www.allrecipes.com/recipe/8493264/noodle-bowl-formula/",
         "https://www.allrecipes.com/recipe/13309/rich-and-simple-french-onion-soup/",
         "https://www.allrecipes.com/recipe/26460/quick-and-easy-chicken-noodle-soup/",
         "https://www.allrecipes.com/recipe/13978/lentil-soup/",
         "https://www.allrecipes.com/recipe/56927/delicious-ham-and-potato-soup/",
         "https://www.allrecipes.com/recipe/8941/slow-cooker-chicken-and-dumplings/",
         "https://www.allrecipes.com/recipe/85055/caldo-de-res-mexican-beef-soup/",
         "https://www.allrecipes.com/recipe/13351/chicken-tortilla-soup-i/",

         "https://www.allrecipes.com/recipe/8538301/crescent-roll-pretzels/",
         "https://www.allrecipes.com/recipe/147954/grandmas-best-zucchini-bread/",
         "https://www.allrecipes.com/recipe/8505902/orange-cinnamon-rolls/",
         "https://www.allrecipes.com/recipe/8514987/chef-johns-parker-house-rolls/",
         "https://www.allrecipes.com/recipe/17891/golden-sweet-cornbread/",
         "https://www.allrecipes.com/recipe/20171/quick-and-easy-pizza-crust/",
         "https://www.allrecipes.com/recipe/215378/classic-dinner-rolls/",
         "https://www.allrecipes.com/recipe/233652/homemade-hamburger-buns/",
         "https://www.allrecipes.com/recipe/6820/downeast-maine-pumpkin-bread/",
         "https://www.allrecipes.com/recipe/220943/chef-johns-buttermilk-biscuits/",

         "https://www.allrecipes.com/recipe/22255/oyster-shooters/",
         "https://www.allrecipes.com/recipe/32446/party-punch-iv/",
         "https://www.allrecipes.com/recipe/32352/best-strawberry-daiquiri/",
         "https://www.allrecipes.com/recipe/42022/easy-apple-cider/",
         "https://www.allrecipes.com/recipe/24494/ultimate-frozen-strawberry-margarita/",
         "https://www.allrecipes.com/recipe/57028/amazingly-good-eggnog/",
         "https://www.allrecipes.com/recipe/233521/classic-canadian-caesar/",
         "https://www.allrecipes.com/recipe/9501/hot-apple-cider/",
         "https://www.allrecipes.com/recipe/222885/grasshopper-cocktail/",
         "https://www.allrecipes.com/recipe/32451/limoncello/",

         "https://www.allrecipes.com/recipe/8542272/carrot-apple-cake-with-cream-cheese-frosting/",
         "https://www.allrecipes.com/recipe/8538411/smores-cookies/",
         "https://www.allrecipes.com/recipe/155222/philadelphia-classic-cheesecake/",
         "https://www.allrecipes.com/recipe/8532800/spotted-dick/",
         "https://www.allrecipes.com/recipe/8529769/5-minute-baileys-chocolate-mousse/",
         "https://www.allrecipes.com/recipe/155027/eggnog-custard/",
         "https://www.allrecipes.com/recipe/8463224/alfajor-cake/",
         "https://www.allrecipes.com/recipe/8465703/blueberry-cream-cheese-wontons/",
         "https://www.allrecipes.com/recipe/219024/best-mud-pie/",
         "https://www.allrecipes.com/recipe/8497586/chocolate-strawberry-cheesecake/"]


def check():
    error = 0
    correct = 0
    wrong_link = []
    for link in LINKS:
        try:
            get_title(link)
            get_ingredient_lines(link)
            steps = get_directions(link)
            ind = 1
            for step in steps:
                print(ind, ") ", step)
                ind += 1
            val = input("enter 0 if wrong else enter anything else:")
            if val == "0":
                x = 6 / 0
            correct += 1
        except:
            wrong_link.append(link)
            error += 1
    print("error: ", error)
    print("correct: ", correct)
    print("error in link: ")
    for link in wrong_link:
        print(link)


if __name__ == '__main__':
    check()

# RESULTS
# error:  0
# correct:  100

# error that was fixed - in link with (word. words) that was separated to two lines because of dot:
# https://www.allrecipes.com/recipe/8462068/sheet-pan-buttermilk-pancakes/
