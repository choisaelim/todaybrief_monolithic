FROM node:12.14
COPY package.json .
RUN npm install
COPY . .
EXPOSE 8081
CMD ["npm", "start"]