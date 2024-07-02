import express from 'express';
import multer from 'multer';
import axios from 'axios';
import fs from 'fs';
import path from 'path';

const app = express();
const port = 3993;

// Set up storage engine
const storage = multer.diskStorage({
  destination: './uploads/',
  filename: function (req, file, cb) {
    cb(null, file.fieldname + '-' + Date.now() + path.extname(file.originalname));
  }
});

// Initialize upload
const upload = multer({
  storage: storage,
  limits: { fileSize: 100000000 }, // 100 MB
  fileFilter: function (req, file, cb) {
    checkFileType(file, cb);
  }
}).single('image');

// Check file type
function checkFileType(file, cb) {
  const filetypes = /jpeg|jpg|png|gif/;
  const extname = filetypes.test(path.extname(file.originalname).toLowerCase());
  const mimetype = filetypes.test(file.mimetype);

  if (mimetype && extname) {
    return cb(null, true);
  } else {
    cb('Error: Images Only!');
  }
}

async function generateResponse(base64Image, prompt) {
  try {
    const response = await axios.post('http://host.docker.internal:11434/api/generate', {
      model: 'qm:latest',
      prompt: prompt,
      images: [base64Image],
      stream: false
    });

    // If the response needs processing to remove backticks, handle it
    let jsonResponse = response.data.response;
    jsonResponse = jsonResponse.replace('```json\n', '').replace('```', '');
    return JSON.parse(jsonResponse);
  } catch (error) {
    console.error('Error generating response:', error);
    throw new Error('Failed to generate response from ollama model.' + error.toString());
  }
}

app.post('/api/evaluate', upload, async (req, res) => {
  const imagePath = req.file.path;
  const prompt = req.body.prompt;

  try {
    // Read the image file and convert it to a base64 string
    const base64Image = fs.readFileSync(path.resolve(imagePath), { encoding: 'base64' });

    // Generate response from the model
    const response = await generateResponse(base64Image, prompt);

    // Delete the uploaded image file
    fs.unlinkSync(imagePath);

    res.json(response);
  } catch (error) {
    // Ensure the uploaded image file is deleted in case of error
    if (fs.existsSync(imagePath)) {
      fs.unlinkSync(imagePath);
    }
    res.status(500).send(error.toString());
  }
});

app.listen(port, () => {
  console.log(`App listening at http://localhost:${port}`);
});
