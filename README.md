# evai


### Setup
- Download [Ollama](https://ollama.com/download)
- Run `Ollama` app, BTW it doesn't have ui, just a background process.
- In the terminal, run `./run_server.sh`
- Run the android app in `qmobile` after changing the baseUrl ip to the host ip.


### Endpoints
- `api/evaluate`

Takes `image` and `prompt` as input and returns `response` and `score`
- image is a multipart file with size limit of 100MB
- prompt is a string

### Example Usages using PostMan or any other tool

<img width="100" src="https://github.com/EsmaeelNabil/evai/blob/main/res/cat.png?raw=true">

if we passed this image 
``` bash
image : cat.png
prompt : "this picture is very bright and has a car inside it"
```

we will get this evaluation

```json
{
  "response": "The image shows a cat, not a car. The lighting in the photo is bright, but it does not depict a vehicle. Therefore, I would give this image a score of 0.",
  "score": 0
}
```


<img width="100" src="https://github.com/EsmaeelNabil/evai/blob/main/res/girl.jpg?raw=true">

if we passed this image 
``` bash
image : girl.jpg
prompt : "this picture is very good in quality and has girl inside it"
```

we will get this evaluation

```json
{
  "response": "The image is of high quality, with a clear and well-lit subject. The girl appears to be in focus, and the background is not overly bright or blurred. The composition of the photo is balanced, and the lighting on the subject's face is soft and flattering. Overall, this image deserves a score of 10 for its quality and presentation.",
  "score": 10
} 
```

<img width="100" src="https://github.com/EsmaeelNabil/evai/blob/main/res/basement.png?raw=true">

if we passed this image 

```bash
image : basement.png
prompt : "Please analyze the attached image and assess its quality for documentation purposes. The image should be clear, not overly dark, and free from significant blurriness. It should be suitable for use in later evaluations of work or installations, even if taken in low-light conditions such as basements. Ensure that the key details are visible and discernible."
```

we will get this evaluation

```json
 {
  "response": "The image provided is of a basement room with various objects and tools. The lighting in the room appears to be low, which may affect visibility. There are several items that can be identified, including what looks like a workbench or table with tools on it, a chair, and some other miscellaneous items. However, due to the low light conditions, some details are not clearly visible. The overall quality of the image for documentation purposes is not ideal, as it may be difficult to discern certain features or assess the condition of the room accurately. A score of 3 would reflect this assessment.",
  "score": 3
}
```
