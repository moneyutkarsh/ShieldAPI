import React, { useEffect, useState, useRef } from 'react';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

export const useSecurityEvents = () => {
    const [events, setEvents] = useState([]);
    const stompClient = useRef(null);

    useEffect(() => {
        const socket = new SockJS('/ws-security');
        stompClient.current = Stomp.over(socket);
        stompClient.current.debug = null; // Disable debug logs

        stompClient.current.connect({}, (frame) => {
            console.log('Connected to Security Stream');
            stompClient.current.subscribe('/topic/security-alerts', (message) => {
                const newEvent = JSON.parse(message.body);
                setEvents((prev) => [
                    {
                        id: Date.now(),
                        type: newEvent.eventType,
                        ip: newEvent.sourceIp,
                        severity: newEvent.severity,
                        time: 'Just now'
                    },
                    ...prev.slice(0, 19) // Keep last 20
                ]);
            });
        }, (error) => {
            console.error('WebSocket Error:', error);
        });

        return () => {
            if (stompClient.current && stompClient.current.connected) {
                try {
                    stompClient.current.disconnect();
                } catch (e) {
                    console.warn('Disconnect error:', e);
                }
            }
        };
    }, []);

    return events;
};
