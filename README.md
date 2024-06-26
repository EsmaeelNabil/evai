# evai


### Setup
- Download [Ollama](https://ollama.com/download)
- Run the app you downloaded, it doesn't have ui, just a background process.
- Pull [LLava](https://ollama.com/library/llava) model from here
  - Run in the terminal `ollama pull llava`
- CD to the project dir
  - Run in the terminal `ollama create qm`

### Usage

<img width="100" src="https://github.com/EsmaeelNabil/evai/blob/main/cat.png?raw=true">

if we passed this image 
``` bash
node main.js cat.png "this picture is very bright and has a car inside it"
```

we will get this evaluation

```json
{
  "response": "The image shows a cat, not a car. The lighting in the photo is bright, but it does not depict a vehicle. Therefore, I would give this image a score of 0.",
  "score": 0
}
```


<img width="100" src="https://github.com/EsmaeelNabil/evai/blob/main/girl.jpg?raw=true">

if we passed this image 
``` bash
node main.js girl.jpg "this picture is very good in quality and has girl inside it"
```

we will get this evaluation

```json
{
  "response": "The image is of high quality, with a clear and well-lit subject. The girl appears to be in focus, and the background is not overly bright or blurred. The composition of the photo is balanced, and the lighting on the subject's face is soft and flattering. Overall, this image deserves a score of 10 for its quality and presentation.",
  "score": 10
} 
```
