const http = require('http');

const PORT = process.env.PORT || 8081;

const server = http.createServer((req, res) => {
    if (req.method === 'POST' && req.url === '/charge') {
        let body = '';
        
        req.on('data', chunk => {
            body += chunk.toString();
        });
        
        req.on('end', () => {
            try {
                const { card_token } = JSON.parse(body);
                
                switch (card_token) {
                    case 'tok_success':
                        setTimeout(() => {
                            res.writeHead(200, { 'Content-Type': 'application/json' });
                            res.end(JSON.stringify({
                                status: 'succeeded',
                                psp_ref: 'psp_' + Date.now()
                            }));
                        }, 100);
                        break;
                        
                    case 'tok_insufficient_funds':
                        setTimeout(() => {
                            res.writeHead(200, { 'Content-Type': 'application/json' });
                            res.end(JSON.stringify({
                                status: 'failed',
                                code: 'insufficient_funds'
                            }));
                        }, 100);
                        break;
                        
                    case 'tok_card_declined':
                        setTimeout(() => {
                            res.writeHead(200, { 'Content-Type': 'application/json' });
                            res.end(JSON.stringify({
                                status: 'failed',
                                code: 'card_declined'
                            }));
                        }, 100);
                        break;
                        
                    case 'tok_timeout':
                        setTimeout(() => {
                            res.writeHead(200, { 'Content-Type': 'application/json' });
                            res.end(JSON.stringify({
                                status: 'succeeded',
                                psp_ref: 'psp_' + Date.now()
                            }));
                        }, 30000);
                        break;
                        
                    case 'tok_network_error':
                        res.destroy();
                        break;
                        
                    default:
                        res.writeHead(400, { 'Content-Type': 'application/json' });
                        res.end(JSON.stringify({
                            status: 'failed',
                            code: 'invalid_token'
                        }));
                }
            } catch (e) {
                res.writeHead(400, { 'Content-Type': 'application/json' });
                res.end(JSON.stringify({ error: 'Invalid request' }));
            }
        });
    } else {
        res.writeHead(404);
        res.end();
    }
});

server.listen(PORT, () => {
    console.log(`Mock PSP running on http://localhost:${PORT}`);
});