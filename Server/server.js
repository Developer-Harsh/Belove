const express = require("express");
const mongoose = require("mongoose");
const bodyParser = require("body-parser");
const cors = require("cors");
const http = require("http");
const socketIo = require("socket.io");
const webrtc = require('wrtc');
const crypto = require("crypto");
const jwt = require("jsonwebtoken");
require('dotenv').config();

const JWT_SECRET = "your_secret_key";

const app = express();
const server = http.createServer(app);

const io = socketIo(server, {
  cors: {
    origin: "*",
    methods: ["GET", "POST"],
  },
});

app.use(cors());
app.use(express.static('public'));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

const streams = new Map();

const PORT = process.env.PORT || 5000;
const MONGO_URI = process.env.MONGO_URI;

mongoose.connect(MONGO_URI).then(() => console.log('Connected to MongoDB'))
    .catch(err => console.log(err));

    const profileSchema = new mongoose.Schema({
        name: { type: String, required: true },
        username: { type: String, required: true, unique: true },
        profile: { type: String, required: true },
        email: { type: String, required: true, unique: true },
        password: { type: String, required: true },
        location: { type: String, required: true },
        gender: { type: String, required: true },
        age: { type: Number, required: true },
        about: { type: String, required: true },
        tokenFCM: { type: String, required: true }
    });

    const Profile = mongoose.model("Profile", profileSchema);

    const roomSchema = new mongoose.Schema({
        roomId: { type: String, required: true },
        userId: { type: String, required: true },
        counts: { type: Number, required: true },
    });

    const Room = mongoose.model("Room", roomSchema);

    app.get('/', (req, res) => {
        res.send('Belove app, client apis');
    });

    app.post("/profiles/register", async (req, res) => {
        const { name, username, profile, email, password, location, gender, age, about, tokenFCM } = req.body;
    
        if (!name || !username || !profile || !email || !password || !location || !gender || !age || !about || !tokenFCM) {
            return res.status(400).json({ error: "All fields are required" });
        }
    
        try {
            const hashedPassword = crypto.createHash("sha256").update(password).digest("hex");
    
            const newProfile = new Profile({
                name,
                username,
                profile,
                email,
                password: hashedPassword,
                location,
                gender,
                age,
                about,
                tokenFCM
            });
    
            await newProfile.save();
            res.status(201).json({ message: "Profile created successfully" });
        } catch (err) {
            if (err.code === 11000) {
                res.status(409).json({ error: "Username or email already exists" });
            } else {
                res.status(500).json({ error: "Server error" });
            }
        }
    });
    
    app.post('/profiles/login', async (req, res) => {
        const { email, password } = req.body;
    
        if (!email || !password) {
            return res.status(400).json({ error: "Email and password are required" });
        }
    
        try {
            const profile = await Profile.findOne({ email });
            
            if (!profile) {
                return res.status(404).json({ error: "Invalid email or password" });
            }
    
            const hashedPassword = crypto.createHash("sha256").update(password).digest("hex");
    
            if (hashedPassword !== profile.password) {
                return res.status(401).json({ error: "Invalid email or password" });
            }
    
            const token = jwt.sign(
                { id: profile._id, username: profile.username },
                JWT_SECRET,
                { expiresIn: "90d" }
            );
    
            const { password: _, ...profileData } = profile.toObject();
    
            res.status(200).json({
                message: "Login successful",
                token,
                user: profileData
            });
        } catch (err) {
            console.error(err);
            res.status(500).json({ error: "Server error" });
        }
    });
    
    app.get("/profiles", async (req, res) => {
        try {
            const profiles = await Profile.find().select("-password");
            res.status(200).json(profiles);
        } catch (err) {
            console.error("Error fetching profiles:", err.message);
            res.status(500).json({ error: "Failed to fetch profiles" });
        }
    });
    
    app.get("/profiles/:username", async (req, res) => {
        const { username } = req.params;
    
        try {
            const profile = await Profile.findOne({ username }).select("-password");
            
            if (!profile) {
                return res.status(404).json({ error: "Profile not found" });
            }
    
            res.status(200).json(profile);
        } catch (err) {
            console.error("Error fetching profile:", err.message);
            res.status(500).json({ error: "Failed to fetch profile" });
        }
    });

    // to update token
    app.patch("/profiles/:username", async (req, res) => {
        const { username } = req.params; // user id
        const updateData = req.body; // update body
    
        try {
            const updatedProfile = await Profile.findOneAndUpdate(
                { username },
                { $set: updateData },
                { new: true, runValidators: true }
            );
    
            if (!updatedProfile) {
                return res.status(404).json({ error: "Profile not found" });
            }
    
            res.status(200).json({ message: "Profile updated successfully", updatedProfile });
        } catch (err) {
            res.status(500).json({ error: "Server error" });
        }
    });

    app.post('/broadcast', async (req, res) => {
        const { streamId, sdp } = req.body;
        console.log('Broadcast received with streamId:', streamId);
    
        if (!streamId) {
            console.error("Stream ID is missing");
            return res.status(400).send("Stream ID is required");
        }
    
        const peer = new webrtc.RTCPeerConnection({
            iceServers: [{ urls: "stun:stun.stunprotocol.org" }]
        });
    
        peer.ontrack = (e) => {
            console.log('Track received for streamId:', streamId);
            streams.set(streamId, e.streams[0]);
        };
    
        const desc = new webrtc.RTCSessionDescription(sdp);
    
        try {
            await peer.setRemoteDescription(desc);
            const answer = await peer.createAnswer();
            await peer.setLocalDescription(answer);
    
            res.json({ sdp: peer.localDescription });
        } catch (err) {
            console.error("Error in /broadcast:", err);
            res.status(500).send("Failed to handle broadcast");
        }
    });
    
    app.post('/consumer', async (req, res) => {
        const { streamId, sdp } = req.body;
        console.log('Consumer requested streamId:', streamId);
    
        if (!streams.has(streamId)) {
            console.error("Stream ID not found:", streamId);
            return res.status(404).send("Stream not found");
        }
    
        const peer = new webrtc.RTCPeerConnection({
            iceServers: [{ urls: "stun:stun.stunprotocol.org" }]
        });
    
        const stream = streams.get(streamId);
        console.log("Retrieved stream for streamId:", streamId);
    
        stream.getTracks().forEach(track => peer.addTrack(track, stream));
    
        const desc = new webrtc.RTCSessionDescription(sdp);
    
        try {
            await peer.setRemoteDescription(desc);
            const answer = await peer.createAnswer();
            await peer.setLocalDescription(answer);
    
            res.json({ sdp: peer.localDescription });
        } catch (err) {
            console.error("Error in /consumer:", err);
            res.status(500).send("Failed to handle consumer");
        }
    });
    
    app.get('/streams', (req, res) => {
        res.json({ activeStreams: Array.from(streams.keys()) });
    });

    // here we will create some function to store room ids and user data

    app.get('/rooms', async (req, res) => {
        try {
            const room = await Room.find();
            res.status(200).json(room);
        } catch (err) {
            console.error("Error fetching room:", err.message);
            res.status(500).json({ error: "Failed to fetch rooms" });
        }
    });

    app.post("/rooms/:roomId", async (req, res) => {
        try {
          const { roomId } = req.params;
          const { userId, counts } = req.body;
      
          const updatedRoom = await Room.findOneAndUpdate(
            { roomId: roomId },
            { userId: userId, counts: counts },
            { new: true, upsert: true }
          );
      
          // Emit the update to all connected clients
          io.emit("room_updated", updatedRoom);
      
          res.status(200).json(updatedRoom);
        } catch (err) {
          res.status(500).send(err.message);
        }
    });

    io.on("connection", (socket) => {
        console.log("A client connected");

        socket.on("disconnect", () => {
          console.log("A client disconnected");
        });
    });

    server.listen(PORT, () => console.log(`Server running on port ${PORT}`));

// Now by using node server.js command it will start local server on your computer machine

// to use this server in your application you will need to get your computer machine's IP address first to run the application

