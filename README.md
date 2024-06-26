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
  "response": "The image shows a cat with a bright and clear background. The lighting is good, and the cat appears to be in focus. There is no car visible in this image. Therefore, I would give it a score of 9 for clarity and detail.",
  "score": 9
}
```


<img width="1512" alt="image" src="https://github.com/EsmaeelNabil/evai/assets/28542963/41abe1d6-5ce0-4205-be60-60d3d5be2dd3">
