import ollama from 'ollama';
import process from 'process';

const args = process.argv.slice(2);

async function generateResponse(imagePath, prompt) {
  try {
    const response = await ollama.generate({
      model: 'qm:latest',
      prompt: prompt,
      images: [imagePath],
      stream: false,
    });

    let jsonResponse = response.response.replace('```json\n', '').replace('```', '');    
    console.log(jsonResponse);

  } catch (error) {
    console.error('Error generating response:', error);
    process.exit(1);
  }
}

function main() {
  if (args.length < 2) {
    console.error('Usage: node script.js <imagePath> <prompt>');
    process.exit(1);
  }

  const [imagePath, prompt] = args;
  generateResponse(imagePath, prompt);
}

main();
