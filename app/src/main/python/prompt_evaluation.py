import openai

# Set up your OpenAI API credentials
openai.api_key = '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'


# Define a function to send a message and get the response
def ask_gpt3_turbo(message):
    response = openai.ChatCompletion.create(
        model="gpt-3.5-turbo",
        temperature=1,
        max_tokens=4000,
        messages=[
            {"role": "user", "content": message}
        ]
    )

    # Get the assistant's reply
    reply = response.choices[0].message['content']
    return reply

propmpts = []
propmpts.append("I have a recipe given between ‘@’ and question about it between ‘$’ :\n@[recipe]@ \n$[question]$\nPlease answer shortly and accuratlly answer in less than 30 tokens")
# Send a message and get the response from the model



if __name__ == '__main__':
    user_message = "what time is it in Israel? "
    response = ask_gpt3_turbo(user_message)
    print("GPT-3.5 Turbo says:", response)

